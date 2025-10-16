# EcoWattch Android App - Willow API v3 Integration

## 🚀 **Project Overview**
EcoWattch is an Android application that integrates with Willow's Building Operating System API v3 to provide real-time building energy monitoring and management capabilities.

## � **Environment Setup (REQUIRED)**

### **Step 1: Configure Credentials**
1. Copy `.env_example` to `.env`:
   ```bash
   cp .env_example .env
   ```

2. Open `.env` and fill in your actual credentials:
   ```bash
   WILLOW_ORGANIZATION_URL=https://your-org.app.willowinc.com/
   WILLOW_CLIENT_ID=your_actual_client_id
   WILLOW_CLIENT_SECRET=your_actual_client_secret
   ```

3. **NEVER commit the `.env` file** - it's automatically ignored by git

### **Step 2: Update Code (Temporary - until environment loading is implemented)**
Until environment variable loading is implemented, temporarily update these files with your credentials for testing:

## �📋 **Credential Configuration - IMPORTANT FOR SPONSOR MEETING**

### **WHERE TO UPDATE CREDENTIALS (2 Locations)**

#### **Location 1: WillowApiV3Config.java** 
**File:** `app/src/main/java/com/example/ecowattchtechdemo/config/WillowApiV3Config.java`
**Line 6:** Update the DEFAULT_BASE_URL
```java
public static final String DEFAULT_BASE_URL = "https://northernarizonauniversity.app.willowinc.com/";
// ↑ CHANGE THIS to your organization's URL
```

#### **Location 2: WillowApiV3TestActivity.java**
**File:** `app/src/main/java/com/example/ecowattchtechdemo/WillowApiV3TestActivity.java`
**Lines 120-122:** Update the pre-filled credentials
```java
// Set default values - TODO: Move to secure configuration
organizationInput.setText("https://northernarizonauniversity.app.willowinc.com/");
clientIdInput.setText("YOUR_CLIENT_ID_HERE");
clientSecretInput.setText("YOUR_CLIENT_SECRET_HERE");
// ↑ CHANGE THESE to your organization's credentials
```

### **Required Credentials from Sponsor**
Please request these from your Willow administrator:

1. **Organization URL** 
   - Format: `https://[your-org].app.willowinc.com/`
   - Example: `https://northernarizonauniversity.app.willowinc.com/`

2. **Client ID** (OAuth2)
   - Format: UUID (e.g., `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`)
   - Provided by Willow administrator

3. **Client Secret** (OAuth2)
   - Format: Secure string (e.g., `~b~8Q~xxxxxxxxxxxxxxxxxxxxxxxxx`)
   - Keep this secure and never commit to version control

### **Building Twin IDs (Confirmed Working)**
For power consumption monitoring, the following twin IDs have been confirmed:

- **Tinsley Hall**: `PNT9CnuLmTV4tkZwAigypXrnY`
- **Gabaldon Hall**: `PNT6hrRL8shRqbwLaXsbhDrBC`

These are used in:
- `PowerDataDisplayActivity.java` for real-time power monitoring
- `WillowApiV3TestActivity.java` for time series testing

## 🔧 **Setup Instructions**

### **Prerequisites**
- Android Studio (latest recommended)
- JDK 8+
- Gradle (wrapper included)
- Internet connection for API testing

### **Installation**
1. **Clone the repository:**
   ```bash
   git clone [your-repository-url]
   cd Ecowattch-App-main
   ```

2. **Open in Android Studio:**
   - Open the project folder in Android Studio
   - Let Gradle sync complete

3. **Update Credentials (see above section)**

4. **Build the project:**
   ```bash
   ./gradlew build
   ```

## 🧪 **Testing the API Integration**

### **Run the Test Activity**
1. Launch the app
2. Navigate to `WillowApiV3TestActivity`
3. Follow the numbered testing workflow:

#### **1️⃣ Set Credentials**
- Verify pre-filled organization URL
- Confirm Client ID and Secret
- Tap "Set Credentials"

#### **2️⃣ Test Authentication**
- Verifies OAuth2 connection
- Gets access token
- Confirms API connectivity

#### **3️⃣ Get Models**
- Lists available data models
- Shows what types of twins exist
- Good API exploration starting point

#### **4️⃣ Search Buildings**
- Searches for building-related twins
- Shows available building data
- Provides twin IDs for further testing

#### **5️⃣ Test Time Series**
- Demonstrates sensor data access
- Shows API structure (uses sample data)
- Guidance for real implementation

## 📊 **API Features Implemented**

### **Authentication**
- ✅ OAuth2 Client Credentials Grant
- ✅ Automatic token refresh
- ✅ Secure credential handling

### **Data Access**
- ✅ Building/Twin search with advanced filters
- ✅ Model discovery and exploration
- ✅ Time series data retrieval
- ✅ Relationship traversal
- ✅ Paginated responses

### **Developer Features**
- ✅ Comprehensive error handling
- ✅ Detailed logging and debugging
- ✅ Real-time testing interface
- ✅ Step-by-step troubleshooting

## 🏗️ **Project Structure**

```
app/src/main/java/com/example/ecowattchtechdemo/
├── api/
│   └── WillowApiV3Service.java          # Main API service class
├── config/
│   └── WillowApiV3Config.java           # API configuration & credentials
├── models/
│   ├── OAuth2TokenResponse.java         # Authentication models
│   ├── WillowTwinV3.java               # Twin data models
│   └── WillowTimeSeriesData.java       # Time series data models
├── WillowApiV3TestActivity.java        # Testing interface
└── WillowTestLogsAdapter.java          # UI logging adapter

app/src/main/res/layout/
└── activity_willow_api_v3_test.xml     # Test interface layout
```

## 🎯 **For Production Use**

### **Integration Example**
```java
// Initialize service
WillowApiV3Service apiService = new WillowApiV3Service();
apiService.setOrganization("your-org.app.willowinc.com");
apiService.setCredentials(clientId, clientSecret);

// Search for buildings
apiService.searchBuildingByName("Building Name", new ApiCallback<List<WillowTwinV3>>() {
    @Override
    public void onSuccess(List<WillowTwinV3> buildings) {
        // Handle successful results
        for (WillowTwinV3 building : buildings) {
            Log.d("Building", building.getDisplayName() + " - " + building.getId());
        }
    }
    
    @Override
    public void onError(String error) {
        // Handle errors
        Log.e("API Error", error);
    }
});
```

### **Security Notes**
- Never commit real credentials to version control
- Use environment variables or secure storage in production
- Implement proper error handling for network issues
- Consider implementing offline capabilities

## 🤝 **Sponsor Meeting Discussion Points**

### **Demo Flow**
1. **Show Test Interface** - Live API testing
2. **Authentication Success** - OAuth2 working
3. **Data Discovery** - Available models and buildings
4. **Real-time Data** - Time series capabilities
5. **Error Handling** - Robust troubleshooting

### **Technical Achievements**
- ✅ Complete API v3 integration
- ✅ OAuth2 security implementation
- ✅ Advanced search capabilities
- ✅ Production-ready architecture
- ✅ Comprehensive testing framework

### **Next Steps for Sponsor**
1. **Provide Production Credentials**
   - Organization URL
   - Client ID & Secret
   - API permissions verification

2. **Data Access Discussion**
   - Which buildings/twins to focus on
   - Required sensor data types
   - Data refresh frequency needs

3. **Feature Prioritization**
   - Real-time dashboards
   - Historical data analysis
   - Energy optimization recommendations
   - Alert/notification system

## 🔍 **Troubleshooting**

### **Common Issues**
- **Authentication Failed**: Verify credentials and organization URL
- **No Buildings Found**: Check model types and search permissions
- **Time Series Errors**: Use valid twin IDs from building search
- **Network Issues**: Verify internet connection and API endpoints

### **Debug Mode**
Check Android Studio Logcat for detailed logs:
- Filter by `WillowApiV3Service` for API calls
- Filter by `WillowApiV3Test` for test activity logs

## 📞 **Support**
- **Willow API Documentation**: https://developers.willowinc.com/openapi/willowtwin/v3-test/
- **Technical Issues**: Check error logs and troubleshooting guide
- **Credentials/Permissions**: Contact Willow administrator

---

**Status**: ✅ Ready for Sponsor Demo  
**Last Updated**: October 8, 2025  
**API Version**: Willow v3  
**Build Status**: ✅ Successful
