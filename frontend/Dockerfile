# Build stage
FROM node:22-bookworm-slim AS build

WORKDIR /app

# Public backend URL used while Vite builds the frontend
ARG VITE_API_BASE_URL=http://localhost:8080
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

# Install dependencies
COPY package.json package-lock.json ./
RUN npm ci --no-audit --no-fund

# Build the production frontend
COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]