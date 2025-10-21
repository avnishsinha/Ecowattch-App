# EcoWattch Android App – Willow API v3 Integration

## 🚀 Project Overview

EcoWattch is an Android application that connects with **Willow’s Building Operating System API v3** to provide real-time energy-monitoring and management features for campus buildings.

---

## 📋 Credential Configuration (Important for Sponsor Demo)

### **Where to Update Credentials – 2 Locations**

#### **1️⃣ `WillowApiV3Config.java`**

```java
public static final String DEFAULT_BASE_URL = "<YOUR_ORG_URL_HERE>";
// Example: https://yourorganization.app.willowinc.com/
```

#### **2️⃣ `WillowApiV3TestActivity.java`**

```java
// Set default values
organizationInput.setText("<YOUR_ORG_URL_HERE>");
clientIdInput.setText("<YOUR_CLIENT_ID>");
clientSecretInput.setText("<YOUR_CLIENT_SECRET>");
// Replace with sponsor-provided credentials
```

### **Credentials Required from Sponsor**

* **Organization URL:**  `https://<org>.app.willowinc.com/`
* **Client ID:**  (UUID format)
* **Client Secret:**  (Secure string)

---

## 🏢 Confirmed Building Twin IDs (for Testing)

Use these *example* twin IDs for verified test runs:

* **Tinsley Hall** → `<TWIN_ID_TINSLEY>`
* **Gabaldon Hall** → `<TWIN_ID_GABALDON>`

---

## 🔧 Setup Instructions

1. Clone repository → `git clone [your-repo-url]`
2. Open in Android Studio → allow Gradle sync.
3. Update credentials (see above).
4. Build → `./gradlew build`.

**Prereqs:** Android Studio (Arctic Fox or later), JDK 8+, Internet access.

---

## 🧪 Testing the Integration

### **Demo Workflow in `WillowApiV3TestActivity`**

1. **Set Credentials** → verify URL & client info.
2. **Test Authentication** → obtain OAuth2 token.
3. **Get Models** → view available data schemas.
4. **Search Buildings** → confirm accessible twins.
5. **Test Time Series** → retrieve sample sensor data.


**Status:** ✅ Alpha-demo ready
**Last Updated:** Oct 2025
**API Version:** Willow v3
**Build Status:** ✅ Stable
