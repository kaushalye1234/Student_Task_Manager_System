# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Copy package configurations and install dependencies
COPY package*.json ./
RUN npm ci

# Copy the rest of the application files and build the production assets
COPY . .
RUN npm run build

# Run stage
FROM nginx:alpine

# Copy built assets to Nginx html directory
COPY --from=build /app/dist /usr/share/nginx/html

# Expose port 80 for HTTP traffic
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
