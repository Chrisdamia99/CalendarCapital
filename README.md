# CalendarCapital

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![GitHub stars](https://img.shields.io/github/stars/YourUsername/YourRepo.svg)](https://github.com/YourUsername/YourRepo/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/YourUsername/YourRepo.svg)](https://github.com/YourUsername/YourRepo/network)
[![GitHub issues](https://img.shields.io/github/issues/YourUsername/YourRepo.svg)](https://github.com/YourUsername/YourRepo/issues)

A simple and effective calendar application that users can schedule their events.

## Features

- **Event Creation:** Create, edit, and delete events with ease.
- **Day, Week, and Month Views:** View your events in different timeframes for better organization.
- **Reminders:** Set reminders for important events to receive notifications.
- **Repeating Events:** Set repeating events easy and read them via the calendar or via ExpandableListView.
- **Custom Repeating Events:** There is capability of making customized repeating events for specific days,weeks,months or years. 
- **Customization:** Personalize your events with colors, labels, and location.
- **Offline Access:** Access your calendar even without an internet connection.
- **User-Friendly Interface:** Intuitive UI for a smooth user experience.

## Screenshots

| MonthView with different color events.              | NavigationDrawer                             |
|-----------------------------------------------------|---------------------------------------------|
| <img class="screenshot" src="https://github.com/Chrisdamia99/CalendarCapital/assets/48257791/8d50e8e8-1569-48d1-a1b2-4794ec4d8d36.png"  width="200"> | <img class="screenshot" src="https://github.com/Chrisdamia99/CalendarCapital/assets/48257791/8a859fc4-e670-421c-98cc-a902722ffc78.png" width="200"> |
| Repeating events and reminder notification bubble  | Customization of adding or updating an event |
| <img class="screenshot" src="https://github.com/Chrisdamia99/CalendarCapital/assets/48257791/7f1b4473-babe-40ac-a69b-7599d35e3aa9.png" width="200"> | <img class="screenshot" src="https://github.com/Chrisdamia99/CalendarCapital/assets/48257791/681e57c3-c299-4771-81de-e34fb56ccf00.png" width="200"> |




<!-- Add more screenshots if necessary -->

## Getting Started

Follow these instructions to get a copy of your project up and running on your local machine for development and testing purposes.

1. **Prerequisites:** Include any prerequisites or dependencies users need to install before using your app.
2. **Installation:** Step-by-step guide on how to install your app.
3. **Usage:** How to use your app, including examples and code snippets.

## Contributing

Contributions are welcome! If you have any ideas, improvements, or bug fixes, please follow these steps:

1. Fork the repository.
2. Create a new branch: `git checkout -b feature/your-feature-name`.
3. Make your changes and commit them: `git commit -m 'Add some feature'`.
4. Push to the branch: `git push origin feature/your-feature-name`.
5. Create a pull request explaining your changes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Java Programming Language.
- Android Studio.
- Udemy Lessons.
- Youtube Programming lessons and examples.

- I thank my colleagues at Unisoft for their valuable help and guidance in critical and non-critical parts of the application.

  
Dependencies:

dependencies {

    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.test:monitor:1.6.1'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
  
    def multidex_version = "2.0.1"
    implementation "androidx.multidex:multidex:$multidex_version"
  
    implementation 'com.makeramen:roundedimageview:2.3.0'

    coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:2.0.3'

    //Navigation Component
    implementation 'androidx.navigation:navigation-fragment-ktx:2.6.0-beta01'
    implementation 'androidx.navigation:navigation-ui-ktx:2.6.0-beta01'
    implementation "androidx.work:work-runtime:2.8.1"

    implementation "androidx.viewpager2:viewpager2:1.0.0"
    implementation 'com.jakewharton.threetenabp:threetenabp:1.4.6'

}

configurations.implementation {
    exclude group: 'org.jetbrains.kotlin', module: 'kotlin-stdlib-jdk8'
}


## Contact

- Christos Damianidis  
- Email: chydami@teiemt.gr
- Project Link: [https://github.com/YourUsername/YourRepo](https://github.com/YourUsername/YourRepo](https://github.com/Chrisdamia99/CalendarCapital.git)https://github.com/Chrisdamia99/CalendarCapital.git)

