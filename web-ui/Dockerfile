# Stage 1: Build
FROM node:alpine AS build-stage
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install -g @angular/cli  # Install Angular CLI globally
RUN npm install   # Install production dependencies
COPY . .
RUN node --version
RUN ng build --configuration production

# Stage 2: Production
FROM nginx:alpine AS production-stage
COPY --from=build-stage /app/dist/web-ui/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["sh", "-c", "envsubst < /usr/share/nginx/html/assets/env.template.js > /usr/share/nginx/html/assets/env.js && nginx -g 'daemon off;'"]
