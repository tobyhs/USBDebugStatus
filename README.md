# USB Debug Status

USB Debug Status is an Android home screen widget that indicates whether USB
debugging is on. Clicking the icon on the widget will open the Developer
options screen.

Clicking the widget's switch will toggle USB debugging. You will need to grant
permissions to your device via adb first:
```sh
adb shell pm grant io.github.tobyhs.usbdebugstatus android.permission.WRITE_SECURE_SETTINGS
```

After adding the widget to your home screen, you may need to restart your phone
for it to work properly. I haven't figured out why this is sometimes necessary.
