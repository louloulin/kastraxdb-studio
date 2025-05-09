// Prevents additional console window on Windows in release, DO NOT REMOVE!!
#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

use log::info;
use serde::{Deserialize, Serialize};
use std::env;
use std::fs;
use std::path::Path;
use std::sync::Mutex;
use tauri::menu::{Menu, MenuItem, Submenu};
use tauri::{AppHandle, Manager, Runtime, Window};

// 状态管理
struct AppState {
    base_url: Mutex<String>,
    force_quit_code: Mutex<bool>,
}

// 应用信息结构体
#[derive(Serialize, Deserialize, Clone)]
struct AppInfo {
    version: String,
    platform: String,
    arch: String,
}

// 平台信息结构体
#[derive(Serialize, Deserialize, Clone)]
struct PlatformInfo {
    is_linux: bool,
    is_win: bool,
    is_mac: bool,
}

// 窗口状态结构体
#[derive(Serialize, Deserialize, Clone)]
struct WindowBounds {
    width: f64,
    height: f64,
    x: f64,
    y: f64,
}

// 菜单项结构体
#[derive(Serialize, Deserialize, Clone)]
struct MenuProps {
    version: Option<String>,
    menus: Option<Vec<MenuInfo>>,
}

#[derive(Serialize, Deserialize, Clone)]
struct MenuInfo {
    label: String,
    key: Option<String>,
    children: Option<Vec<MenuChild>>,
}

#[derive(Serialize, Deserialize, Clone)]
struct MenuChild {
    label: String,
    key: String,
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

// 获取平台信息
#[tauri::command]
fn get_platform() -> PlatformInfo {
    PlatformInfo {
        is_linux: env::consts::OS == "linux",
        is_win: env::consts::OS == "windows",
        is_mac: env::consts::OS == "macos",
    }
}

// 设置基础 URL
#[tauri::command]
fn set_base_url(state: tauri::State<AppState>, url: String) {
    let mut base_url = state.base_url.lock().unwrap();
    *base_url = url;
    info!("设置基础 URL: {}", *base_url);
}

// 设置强制退出代码
#[tauri::command]
fn set_force_quit_code(state: tauri::State<AppState>, code: bool) {
    let mut force_quit_code = state.force_quit_code.lock().unwrap();
    *force_quit_code = !code;
    info!("设置强制退出代码: {}", *force_quit_code);
}

// 启动服务器
#[tauri::command]
async fn start_server_for_spawn() -> Result<serde_json::Value, String> {
    info!("收到启动服务器请求");
    // 在开发模式下，我们不需要启动 Java 服务器，因为它已经由 npm run start:web 启动了
    Ok(serde_json::json!({
        "success": true,
        "message": "服务器已启动"
    }))
}

// 获取产品名称
#[tauri::command]
fn get_product_name() -> String {
    env!("CARGO_PKG_NAME").to_string()
}

// 读取版本
#[tauri::command]
fn read_version() -> String {
    let mut version = String::new();
    if cfg!(not(debug_assertions)) {
        let version_path = Path::new("./versions/version");
        if version_path.exists() {
            version = fs::read_to_string(version_path).unwrap_or_default();
        }
    }
    version.trim().to_string()
}

// 创建应用程序菜单
#[tauri::command]
fn register_app_menu<R: Runtime>(app: AppHandle<R>, menu_props: MenuProps) -> Result<(), String> {
    info!("创建应用程序菜单");

    // 只在 macOS 上创建菜单
    if !cfg!(target_os = "macos") {
        return Ok(());
    }

    let mut menu = Menu::new();

    // 添加应用程序菜单
    let app_menu = Submenu::new(
        "MagicDB",
        Menu::new()
            .add_item(MenuItem::new("关于MagicDB", "about", |_| {}))
            .add_native_item(MenuItem::Separator)
            .add_item(MenuItem::new("重新启动", "restart", |_| {}))
            .add_item(MenuItem::new("退出", "quit", |_| {})),
    );
    menu = menu.add_submenu(app_menu);

    // 添加自定义菜单
    if let Some(menus) = menu_props.menus {
        for menu_info in menus {
            let mut submenu = Menu::new();

            if let Some(children) = menu_info.children {
                for child in children {
                    let item = MenuItem::new(&child.label, &child.key, |_| {});
                    submenu = submenu.add_item(item);
                }
            }

            menu = menu.add_submenu(Submenu::new(menu_info.label, submenu));
        }
    }

    // 添加编辑菜单
    let edit_menu = Submenu::new(
        "编辑",
        Menu::new()
            .add_native_item(MenuItem::Undo)
            .add_native_item(MenuItem::Redo)
            .add_native_item(MenuItem::Separator)
            .add_native_item(MenuItem::Cut)
            .add_native_item(MenuItem::Copy)
            .add_native_item(MenuItem::Paste)
            .add_native_item(MenuItem::SelectAll),
    );
    menu = menu.add_submenu(edit_menu);

    // 添加视图菜单
    let view_menu = Submenu::new(
        "视图",
        Menu::new()
            .add_native_item(MenuItem::Separator)
            .add_item(MenuItem::new("放大", "zoom-in", |_| {}))
            .add_item(MenuItem::new("缩小", "zoom-out", |_| {}))
            .add_item(MenuItem::new("重置", "zoom-reset", |_| {}))
            .add_native_item(MenuItem::Separator)
            .add_native_item(MenuItem::ToggleFullScreen),
    );
    menu = menu.add_submenu(view_menu);

    // 添加窗口菜单
    let window_menu = Submenu::new(
        "窗口",
        Menu::new()
            .add_native_item(MenuItem::Minimize)
            .add_item(MenuItem::new("关闭", "close", |_| {})),
    );
    menu = menu.add_submenu(window_menu);

    // 添加帮助菜单
    let help_menu = Submenu::new(
        "帮助",
        Menu::new()
            .add_item(MenuItem::new("打开日志", "open-log", |_| {}))
            .add_item(MenuItem::new("打开控制台", "open-devtools", |_| {}))
            .add_item(MenuItem::new("访问官网", "visit-website", |_| {}))
            .add_item(MenuItem::new("查看文档", "view-docs", |_| {}))
            .add_item(MenuItem::new("查看更新日志", "view-changelog", |_| {})),
    );
    menu = menu.add_submenu(help_menu);

    // 设置应用程序菜单
    app.set_menu(menu).map_err(|e| e.to_string())?;

    Ok(())
}

// 主函数
fn main() {
    // 初始化日志
    env_logger::init();

    // 创建应用程序
    tauri::Builder::default()
        .plugin(tauri_plugin_shell::init())
        .plugin(tauri_plugin_dialog::init())
        .plugin(tauri_plugin_fs::init())
        .plugin(tauri_plugin_window::init())
        .plugin(tauri_plugin_http::init())
        .plugin(tauri_plugin_process::init())
        .plugin(tauri_plugin_os::init())
        .manage(AppState {
            base_url: Mutex::new(String::new()),
            force_quit_code: Mutex::new(false),
        })
        .invoke_handler(tauri::generate_handler![
            get_app_info,
            get_platform,
            set_base_url,
            set_force_quit_code,
            start_server_for_spawn,
            get_product_name,
            read_version,
            register_app_menu,
        ])
        .setup(|app| {
            // 在开发模式下打开开发者工具
            #[cfg(debug_assertions)]
            {
                if let Some(window) = app.get_webview_window("main") {
                    window.open_devtools();
                }
            }

            Ok(())
        })
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
