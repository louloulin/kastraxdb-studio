// Prevents additional console window on Windows in release, DO NOT REMOVE!!
#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

use serde::{Deserialize, Serialize};
use std::env;

// 应用信息结构体
#[derive(Serialize, Deserialize, Clone)]
struct AppInfo {
    version: String,
    platform: String,
    arch: String,
}

// 获取应用信息
#[tauri::command]
fn get_app_info() -> AppInfo {
    AppInfo {
        version: env!("CARGO_PKG_VERSION").to_string(),
        platform: env::consts::OS.to_string(),
        arch: env::consts::ARCH.to_string(),
    }
}

// 主函数
fn main() {
    // 创建应用程序
    tauri::Builder::default()
        .plugin(tauri_plugin_shell::init())
        .plugin(tauri_plugin_dialog::init())
        .plugin(tauri_plugin_fs::init())
        .invoke_handler(tauri::generate_handler![get_app_info,])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
