#!/usr/bin/env node

import {program} from 'commander';
import {exec} from 'child_process';
import axios from 'axios';
import FormData from 'form-data';
import fs from 'fs';
import path from 'path';
import YAML from 'js-yaml';

program
    .name("nisaba-cli")
    .description("CLI to bundle OpenAPI definitions and upload them to a server")
    .option('-s, --source-file <type>', 'Source OpenAPI definition file (optional)')
    .option('-p, --project-id <type>', 'Project ID')
    .option('-n, --name <type>', 'Version Name (optional)', 'main')
    .option('-t, --token <type>', 'Authentication token')
    .option('-b, --base-url <type>', 'Base URL of the server')
    .parse(process.argv);

const options = program.opts();

if (!options.projectId || !options.token || !options.baseUrl) {
    console.error('Missing required arguments: --project-id, --token, and --base-url are required.');
    program.help();
}

const bundledFiles = [];

function isValidAPIDocument(filePath) {
    if (!fs.existsSync(filePath)) {
        console.error(`The file ${filePath} does not exist.`);
        return false;
    }

    const content = fs.readFileSync(filePath, 'utf8');
    try {
        const doc = YAML.load(content);
        return doc.openapi || doc.swagger || doc.asyncapi;
    } catch (error) {
        console.error(`Error processing ${filePath}: ${error.message}`);
        return false;
    }
}

function isAsyncAPIDocument(filePath) {
    if (!fs.existsSync(filePath)) {
        console.warn(`The file ${filePath} does not exist.`);
        return false;
    }

    const content = fs.readFileSync(filePath, 'utf8');
    try {
        const doc = YAML.load(content);
        return doc.asyncapi;
    } catch (error) {
        console.error(`Error processing ${filePath}: ${error.message}`);
        return false;
    }
}

function bundleOpenAPIDocument(filePath) {
    return new Promise((resolve, reject) => {
        const outputFilePath = path.basename(filePath, '.yaml') + '_bundled.yaml';
        bundledFiles.push(outputFilePath);  // Track the bundled file for later deletion
        exec(`redocly bundle ${filePath} -o ${outputFilePath}`, (error) => {
            if (error) {
                console.error(`Error bundling ${filePath}: ${error}`);
                return reject(error);
            }
            resolve(outputFilePath);
        });
    });
}

async function prepareOpenAPIDocuments(sourceFilePath) {
    const formData = new FormData();
    let filesToProcess = [];

    if (sourceFilePath) {
        filesToProcess = [sourceFilePath]; // Convert the single string into an array
    } else {
        filesToProcess = fs.readdirSync(process.cwd()).filter(file => file.endsWith('.yaml') && isValidAPIDocument(file)); // Read directory files
    }

    if (filesToProcess.length === 0) {
        throw new Error('No valid OpenAPI definition files found.');
    }

    let hasFilesToUpload = false;
    for (const filePath of filesToProcess) {
        if (!fs.existsSync(filePath)) {
            console.warn(`The file ${filePath} does not exist. Skipping.`);
            continue;
        }

        if (!isValidAPIDocument(filePath)) {
            console.warn(`The file ${filePath} is not a valid OpenAPI document. Skipping.`);
            continue;
        }

        const fileToUpload = await (isAsyncAPIDocument(filePath)
            ? Promise.resolve(filePath)
            : bundleOpenAPIDocument(filePath));

        formData.append('files', fs.createReadStream(fileToUpload), path.basename(fileToUpload));
        hasFilesToUpload = true;
        console.log(`Prepared ${fileToUpload} for upload.`);
    }

    if (!hasFilesToUpload) {
        throw new Error('No valid OpenAPI documents to upload.');
    }

    return formData;
}
async function uploadFileToServer(formData, projectId, versionName, baseUrl, token) {
    const url = `${baseUrl}/api/project-versions/ci`;
    formData.append('projectId', projectId);
    formData.append('versionName', versionName);
    formData.append('token', token);

    try {
        const response = await axios.post(url, formData, {
            headers: formData.getHeaders(),
        });

        if (response.status !== 200) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        console.log('File uploaded successfully:', response.data);
    } catch (error) {
        console.error(error);
        console.error('Error uploading file:', error.response ? error.response.data : error.message);
    }
}

// Function to delete the bundled files
function cleanupBundledFiles() {
    for (const file of bundledFiles) {
        fs.unlinkSync(file);
        console.log(`Deleted ${file}`);
    }
}

const {sourceFile, projectId, token, name, baseUrl} = options;

prepareOpenAPIDocuments(sourceFile)
    .then(formData => uploadFileToServer(formData, projectId, name, baseUrl, token))
    .then(() => cleanupBundledFiles())
    .catch(error => {
        cleanupBundledFiles();
        console.error(error)
    });
