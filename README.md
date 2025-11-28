# 🍽️ PlateShare: Connecting Food Donors with NGOs

PlateShare is a sustainable Android application built with Jetpack Compose that aims to reduce food waste and combat hunger by creating a seamless platform for connecting individuals and businesses with surplus food (Donors) to Non-Governmental Organizations (NGOs) who can distribute it to those in need.

## ✨ Features

This application implements a complete multi-user flow with a focus on modern UI/UX and responsive design:

### General & UI
* **Modern UI:** Built entirely using **Jetpack Compose** with Material 3 theming.
* **Responsive Layout:** Uses `WindowSizeClass` to adapt the layout for mobile (Compact) and tablet/desktop (Expanded) views, especially on the **Donations Screen**.
* **Secure Flow:** Dedicated login and signup screens for both Donor and NGO user types.
* **Navigation:** Uses Jetpack Navigation Compose for all screen transitions.

### Donor Flow
* **Donor Dashboard:** A dedicated screen for Donors to view and manage their active food listings.
* **Add Donation:** A form screen for Donors to quickly list surplus food details (title, description, quantity, expiry, type).

### NGO Flow
* **Donations Screen:** Central hub for NGOs to browse available food listings.
* **Filtering & Sorting:** Includes a dedicated side panel (Expanded view) or modal bottom sheet (Compact view) to filter listings by location, distance, priority, and food type preferences/rejections.
* **Claiming:** Functionality placeholders for claiming available donations.

## 🚀 Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

* Android Studio Jellyfish (or newer)
* Android SDK (API Level 34 or higher)
* Kotlin 1.9.0+

### Installation

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/NirvikJana007/PlateShare.git](https://github.com/NirvikJana007/PlateShare.git)
    cd PlateShare
    ```

2.  **Open in Android Studio:**
    * Open Android Studio and select **File** > **Open**.
    * Navigate to the cloned `PlateShare` directory and click **OK**.

3.  **Run the App:**
    * Sync Gradle (if prompted).
    * Select an emulator or a physical device running Android API 34+ and click the **Run** button (▶️).

## 🧩 Project Structure & Technologies

| Layer | Files/Components | Description |
| :--- | :--- | :--- |
| **UI Framework** | Jetpack Compose, Material 3 | All UI components, styling, and theming. |
| **Navigation** | `NavHost`, `rememberNavController` | Manages app flow between different screens (`Screen` sealed class defines routes). |
| **State Management** | `remember`, `rememberSaveable`, `FilterState.Saver` | Manages local UI state and ensures complex filter objects persist across configuration changes (e.g., rotation). |
| **Data/Dummy** | `Donation` data class, `dummyDonations`, `fetchDonations` | Uses dummy data and simulated asynchronous fetching logic to demonstrate the loading and error states. |
| **Responsiveness** | `WindowWidthSizeClass` | Determines layout adaptation for different screen sizes (e.g., in `DonationsScreen`). |

## 🗺️ Screen Flow Overview

The app's navigation is defined by the `Screen` sealed class:

1.  **Home** (`home`)
    * Can navigate to `DonorLogin`, `NgoLogin`, `DonorSignUp`, `AboutUs`, `Donations`.
2.  **Donor Login** (`donor_login`) $\rightarrow$ **Donor Dashboard** (`donor_dashboard`)
    * From Dashboard $\rightarrow$ **Add Donation** (`add_donation`)
3.  **NGO Login** (`ngo_login`) $\rightarrow$ **Donations** (`donations`)
4.  **Sign Up** (`donor_signup`, `ngo_signup`) $\rightarrow$ Back to respective Login screens.

## 🤝 Contribution

Contributions are welcome! If you find a bug or want to suggest an improvement, please open an issue or submit a pull request.

## 📄 License

This project is licensed under the MIT License - see the `LICENSE` file for details (Note: A separate `LICENSE` file is needed for official projects).
