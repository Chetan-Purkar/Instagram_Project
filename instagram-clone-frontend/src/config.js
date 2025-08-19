const config = {
    API_BASE_URL: process.env.REACT_APP_API_BASE_URL || "http://localhost:8080/api",
    AUTH_TOKEN_KEY: "auth_token", // LocalStorage key for storing auth token
    IMAGE_UPLOAD_URL: process.env.REACT_APP_IMAGE_UPLOAD_URL || "http://localhost:8080/api/upload",
    SOCKET_SERVER_URL: process.env.REACT_APP_SOCKET_SERVER_URL || "http://localhost:8080",
  };
  
  export default config;
  