use std::{collections::HashMap, fmt::Debug};

use serde::{de, de::Error as Error_, Deserialize, Serialize};
use serde::de::DeserializeOwned;
use serde_json::Value;
pub use url::Url;

pub trait Notification {
    type Params: DeserializeOwned + Serialize;
    const METHOD: &'static str;
}

pub trait Request {
    type Params: DeserializeOwned + Serialize;
    type Result: DeserializeOwned + Serialize;
    const METHOD: &'static str;
}