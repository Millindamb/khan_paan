# 🍽️ Khan Paan — Food Ordering App (User)

Khan Paan is a modern Android food ordering application that allows users to browse
a restaurant menu, add items to their cart, place orders, and track their order
history — all in real time. The app solves the problem of inefficient manual food
ordering by providing a seamless digital experience from browsing to checkout,
with live order status updates and persistent user profiles.

---

## 👥 Team Members
 
| Name               | Role                                                                         |
|--------------------|------------------------------------------------------------------------------|
| **Mustafa qureshi** | Frontend developer, UX Design                                               |
| **Millind Amb**    | Backend developer                                                            |
| **Prince Jaiswal** | Frontend developer, Database Design ,UI Figma, Documentation and artifacts   |
| **Ritika Panwar**  | UI Sketches                                                                  |

---

## 🛠️ Tech Stack

| Category       | Technology                                      |
|----------------|-------------------------------------------------|
| Language       | Kotlin                                          |
| Framework      | Android SDK (min SDK 24, target SDK 36)         |
| Architecture   | Activity + Fragment based                       |
| Database       | Supabase (PostgreSQL)                           |
| Authentication | Firebase Authentication (Email + Google Sign-In)|
| Image Loading  | Glide 5.0.5                                     |
| Networking     | Supabase Kotlin SDK (BOM 3.1.4), Ktor Android   |
| Serialization  | Kotlin Serialization JSON 1.6.0                 |
| Image Slider   | ImageSlideshow 0.1.2                            |
| Navigation     | AndroidX Navigation Fragment KTX 2.7.0          |
| Build Tool     | Gradle (Kotlin DSL)                             |

---

## ⚙️ Setup and Run Instructions

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11+
- Android SDK 36 installed (Tools → SDK Manager)
- A Supabase account and project
- A Firebase project with Authentication enabled

### Step 1 — Clone the repository
```bash
git clone https://github.com/your-username/khan-paan-user.git
cd khan-paan-user
```

### Step 2 — Configure Firebase
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a project and enable **Email/Password** and **Google Sign-In**
3. Download `google-services.json`
4. Place it in `app/` directory

### Step 3 — Configure Supabase
1. Go to [supabase.com](https://supabase.com) and create a project
2. Open `app/src/main/java/com/example/gharkakhana/network/SupabaseClient.kt`
3. Replace the placeholder values:
```kotlin
val client = createSupabaseClient(
    supabaseUrl = "YOUR_PROJECT_URL",
    supabaseKey = "YOUR_ANON_KEY"
) { ... }
```

### Step 4 — Create required Supabase tables
Run these in your Supabase SQL Editor:
```sql
-- Users table
CREATE TABLE users (
    id TEXT PRIMARY KEY,
    name TEXT, email TEXT, address TEXT, phone TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Menu table
CREATE TABLE menu (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    food_name TEXT, food_price TEXT,
    food_description TEXT, food_image TEXT,
    food_ingredient TEXT, created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Cart table
CREATE TABLE cart (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id TEXT, food_name TEXT, food_price TEXT,
    food_description TEXT, food_image TEXT,
    food_ingredient TEXT, food_quantity INT DEFAULT 1,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Order Details table
CREATE TABLE order_details (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id TEXT, user_name TEXT,
    food_names TEXT, food_prices TEXT,
    food_images TEXT, food_quantities TEXT,
    address TEXT, phone TEXT, total_amount TEXT,
    order_accepted BOOLEAN DEFAULT false,
    payment_received BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Order History table
CREATE TABLE order_history (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id TEXT, user_name TEXT,
    food_names TEXT, food_prices TEXT,
    food_images TEXT, food_quantities TEXT,
    address TEXT, phone TEXT, total_amount TEXT,
    order_accepted BOOLEAN DEFAULT false,
    payment_received BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

### Step 5 — Enable RLS policies
```sql
-- Run for each table: users, menu, cart, order_details, order_history
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Allow all" ON users FOR ALL TO anon USING (true) WITH CHECK (true);
-- Repeat for remaining tables
```

### Step 6 — Build and run
1. Open the project in Android Studio
2. Click **File → Sync Project with Gradle Files**
3. Select your device or emulator (API 24+)
4. Click ▶ **Run**

---

## 🎬 Demo Video
https://drive.google.com/file/d/1Sz8Sd5_-yTm6YqKAyQFL73ZNupHAe4uD/view?usp=drive_link
---

## 📸 Screenshots

<table>
  <tr>
    <td align="center">
      <img src="https://raw.githubusercontent.com/Millindamb/khan_paan/main/Screenshots/Home.jpeg" width="220"/><br/>
      <b>🏠 Home Screen</b>
    </td>
    <td align="center">
      <img src="https://raw.githubusercontent.com/Millindamb/khan_paan/main/Screenshots/Menu.jpeg" width="220"/><br/>
      <b>🍽️ Menu</b>
    </td>
    <td align="center">
      <img src="https://raw.githubusercontent.com/Millindamb/khan_paan/main/Screenshots/Search.jpeg" width="220"/><br/>
      <b>🔍 Search</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://raw.githubusercontent.com/Millindamb/khan_paan/main/Screenshots/Cart.jpeg" width="220"/><br/>
      <b>🛒 Cart</b>
    </td>
    <td align="center">
      <img src="https://raw.githubusercontent.com/Millindamb/khan_paan/main/Screenshots/Checkout.jpeg" width="220"/><br/>
      <b>💳 Checkout</b>
    </td>
    <td align="center">
      <img src="https://raw.githubusercontent.com/Millindamb/khan_paan/main/Screenshots/OrderHistory.jpeg" width="220"/><br/>
      <b>📦 Order History</b>
    </td>
  </tr>
  <tr>
    <td align="center" colspan="3">
      <img src="https://raw.githubusercontent.com/Millindamb/khan_paan/main/Screenshots/Profile.jpeg" width="220"/><br/>
      <b>👤 Profile</b>
    </td>
  </tr>
</table>
---

## ⚠️ Known Limitations & Future Work

### Current Limitations
- Passwords are managed entirely by Firebase Auth — no in-app password change
- Images are stored as public URLs in Supabase Storage — no private image support
- Payment is Cash on Delivery only — no online payment gateway integrated

### Future Work
- [ ] Integrate online payment (Razorpay / Stripe)
- [ ] Add ratings and reviews for menu items
- [ ] Dark mode support
- [ ] Multi-language support (Hindi, English)
- [ ] Loyalty points and discount coupon system
