# USB Debug Status

USB Debug Status is an Android home screen widget that indicates whether USB
debugging is on. Clicking the icon on the widget will open the Developer
options screen.

Clicking the widget's switch will toggle USB debugging. In order to enable the
switch, you need to grant permissions to your device via adb first:
```sh
adb shell pm grant io.github.tobyhs.usbdebugstatus android.permission.WRITE_SECURE_SETTINGS
```
After running the above command, you may need to re-add the widget.

After adding the widget to your home screen, you may need to restart your phone
for it to work properly. I haven't figured out why this is sometimes necessary.

### Battery Saver

The widget does not update when using Batter Saver on some phones.
In order to work around this, you can open the `App Info` screen, tap `App battery usage`, and select `Unrestricted`. If you don't see this setting, you can use adb:
```sh
adb shell dumpsys deviceidle whitelist +io.github.tobyhs.usbdebugstatus
```
