# Stage 1: Build
FROM node:22 AS build

# Set working directory
WORKDIR /app

# Copy package*.json files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy application code
COPY . .

# Set build-time environment variable
ARG VITE_API_URL=http://localhost:8080
ENV VITE_API_URL=$VITE_API_URL

# Build the application
RUN npm run build

# Stage 2: Production
FROM nginx:alpine

# Set working directory
WORKDIR /usr/share/nginx/html

# Copy built application from build stage
COPY --from=build /app/dist .

# Copy custom nginx configuration if needed
# COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80
EXPOSE 80

# Run Nginx
CMD ["nginx", "-g", "daemon off;"]