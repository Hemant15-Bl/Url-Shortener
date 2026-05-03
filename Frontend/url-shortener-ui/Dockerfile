# Stage 1: Build the react app
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install --legacy-peer-deps
COPY . .
RUN npm run build

#  Stage 2: Server the Nginx
FROM nginx:stable-alpine

# Copy Our Custom Config.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built file from stage 1
COPY --from=build /app/build /usr/share/nginx/html

EXPOSE 80

CMD [ "nginx", "-g", "daemon off;" ]
