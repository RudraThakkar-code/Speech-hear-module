use axum::{routing::get, Json, Router};
use serde::Serialize;
use std::{net::SocketAddr, time::SystemTime};
use tracing_subscriber::EnvFilter;

#[derive(Serialize)]
struct HealthResponse {
    status: &'static str,
    service: &'static str,
    timestamp: u64,
}

async fn health() -> Json<HealthResponse> {
    let timestamp = SystemTime::now()
        .duration_since(SystemTime::UNIX_EPOCH)
        .map(|d| d.as_secs())
        .unwrap_or_default();
    Json(HealthResponse { status: "ok", service: "speech-clinical-api", timestamp })
}

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt()
        .with_env_filter(EnvFilter::from_default_env())
        .init();

    let app = Router::new().route("/health", get(health));
    let address = SocketAddr::from(([127, 0, 0, 1], 8081));
    let listener = tokio::net::TcpListener::bind(address).await.expect("bind failed");
    tracing::info!(?address, "Rust clinical API started");
    axum::serve(listener, app).await.expect("server failed");
}
