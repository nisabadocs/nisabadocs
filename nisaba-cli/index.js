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

function isOpenAPIDocument(filePath) {
    if (!fs.existsSync(filePath)) {
        console.error(`The file ${filePath} does not exist.`);
        return false;
    }

    const content = fs.readFileSync(filePath, 'utf8');
    try {
        const doc = YAML.load(content);
        return doc.openapi || doc.swagger;
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

    if (sourceFilePath) {
        if (!fs.existsSync(sourceFilePath)) {
            throw new Error(`The source file ${sourceFilePath} does not exist.`);
        }
        if (!isOpenAPIDocument(sourceFilePath)) {
            throw new Error(`The file ${sourceFilePath} is not a valid OpenAPI document.`);
        }

        const bundledFilePath = await bundleOpenAPIDocument(sourceFilePath);
        formData.append('files', fs.createReadStream(bundledFilePath), path.basename(sourceFilePath));
        return formData;
    } else {
        const files = fs.readdirSync(process.cwd());
        const openAPIFiles = files.filter(file => file.endsWith('.yaml') && isOpenAPIDocument(file));
        if (openAPIFiles.length === 0) {
            throw new Error('No valid OpenAPI definition files found in the current directory.');
        }

        for (const file of openAPIFiles) {
            const bundledFilePath = await bundleOpenAPIDocument(file);
            formData.append('files', fs.createReadStream(bundledFilePath), path.basename(file));
            console.log(`Bundled and prepared ${file} for upload.`);
        }
        return formData;
    }
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
