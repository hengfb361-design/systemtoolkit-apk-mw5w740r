# System Toolkit

បង្កើតដោយ **AppForge** · System App (Android) · `com.example.systemtoolkit`

## របៀបបង្កើត APK (ឥតគិតថ្លៃ ~៣ នាទី)

1. បង្កើត repo ថ្មីនៅ GitHub (Public ឬ Private ក៏បាន)។
2. Upload ឯកសារទាំងអស់ក្នុង ZIP នេះទៅ root របស់ repo
   (ត្រូវឃើញ `settings.gradle.kts` និងថត `.github/` នៅ root)។
3. ចូល **Actions** → *Build APK* → រង់ចាំ ~២–៤ នាទី។
4. ចុចលើ run ដែលជោគជ័យ → **Artifacts** → ទាញយក `systemtoolkit-apk.zip`។
5. ពន្លាត → បានឯកសារ `app-debug.apk` → ដំឡើងលើទូរស័ព្ទ
   (ត្រូវបើក "Install unknown apps" សម្រាប់ browser ដែលអ្នកប្រើ)។

## បើ build មិនជោគជ័យ

- Java/Gradle ខុស៖ workflow ប្រើ JDK 17 + Gradle 8.7 ស្រាប់ មិនត្រូវកែទេ។
- បើអ្នកប្តូរ `compileSdk` ត្រូវប្តូរ `buildToolsVersion`/AGP ឱ្យត្រូវគ្នា។
- Log កំហុសនៅ Actions → ចុច step ពណ៌ក្រហម។

## ចំណាំ

- APK នេះជា **debug** — ដំឡើងលើទូរស័ព្ទបាន តែដាក់លើ Play Store មិនបានទេ
  (ត្រូវការ release keystore + AAB)។
- បើ App ប្រើអ៊ីនធឺណិត ត្រូវមាន `<uses-permission android:name="android.permission.INTERNET" />`
  ដែលបានដាក់ឱ្យរួចហើយ។
- Package name: `com.example.systemtoolkit` · Version 1.0 (1)

## System app — អ្វីដែលដំណើរការ និងអ្វីដែលត្រូវការ root

- **ដំណើរការធម្មតា (គ្មាន root):** Device Admin, Overlay (សុំសិទ្ធិក្នុង Settings),
  Foreground service និងការចាប់ផ្ដើមពេលបើកទូរស័ព្ទ, ការសរសេរ Settings ដែលអនុញ្ញាត,
  ការសម្អាត background processes និងការលើកលែង battery optimization។
- **ត្រូវការ root៖** `SystemOps.runAsRoot(...)` និងពាក្យបញ្ជា `su -c`, ការដំឡើងជា
  system app ក្នុង `/system/priv-app`, និង permission កម្រិត signature (platform key)។
- App ធម្មតាដំឡើងជា user app មិនអាចទទួល permission កម្រិត signature បានទេ —
  ត្រូវ sign ដោយ key របស់ ROM ឬដាក់ក្នុង `/system/priv-app` ដោយមាន root/custom ROM។
- កូដទាំងអស់នៅក្នុង `app/src/main/java/...` — អ្នកអាចបន្ថែម file ថ្មីក្នុង IDE
  ហើយ Gradle នឹង compile វាដោយស្វ័យប្រវត្តិ។
