# MagicDB Tauri Migration Guide

This document provides information about the migration of MagicDB from Electron to Tauri.

## Why Tauri?

Tauri offers several advantages over Electron:

1. **Smaller bundle size**: Tauri applications are significantly smaller than Electron applications because they use the system's native WebView instead of bundling Chromium.
2. **Better performance**: Tauri applications use less memory and CPU resources.
3. **Enhanced security**: Tauri has a more secure architecture with fine-grained permissions.
4. **Native look and feel**: Tauri applications look and feel more native on each platform.

## Migration Steps

The migration from Electron to Tauri involves the following steps:

1. **Initialize Tauri project structure**:
   - Create Tauri configuration files
   - Set up the Rust backend structure

2. **Migrate main process functionality**:
   - Convert Electron IPC handlers to Tauri commands
   - Implement window management in Tauri
   - Implement menu functionality in Tauri

3. **Update frontend code**:
   - Replace Electron API calls with Tauri API calls
   - Update the build configuration

4. **Update build and packaging scripts**:
   - Update package.json scripts
   - Configure Tauri build settings

## Project Structure

The migrated project has the following structure:

- `src/`: Frontend code (React, TypeScript)
- `src-tauri/`: Tauri backend code (Rust)
  - `src/`: Rust source code
  - `Cargo.toml`: Rust dependencies
  - `tauri.conf.json`: Tauri configuration
  - `build.rs`: Tauri build script

## Running the Application

To run the application in development mode:

```bash
npm run start
```

This will start both the frontend development server and the Tauri application.

To build the application for production:

```bash
npm run build
```

This will build both the frontend and the Tauri application.

## API Compatibility

To minimize code changes, we've created a compatibility layer in `src/utils/tauri-api.ts` that provides an API similar to the Electron API but uses Tauri under the hood.

## Known Issues

- Menu customization is more limited in Tauri compared to Electron
- Some Electron-specific features may not be available in Tauri

## Future Improvements

- Optimize Rust code for better performance
- Add more Tauri-specific features
- Improve error handling

## References

- [Tauri Documentation](https://tauri.app/v1/guides/)
- [Tauri API Reference](https://tauri.app/v1/api/js/)
- [Rust Documentation](https://doc.rust-lang.org/book/)
