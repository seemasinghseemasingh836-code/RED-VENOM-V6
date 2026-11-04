
import os
import time
import shutil
import platform

from kivy.app import App
from kivy.clock import Clock
from kivy.core.window import Window
from kivy.metrics import dp
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.gridlayout import GridLayout
from kivy.uix.label import Label
from kivy.uix.button import Button
from kivy.uix.scrollview import ScrollView
from kivy.uix.popup import Popup
from kivy.uix.textinput import TextInput

Window.clearcolor = (0.025, 0.025, 0.035, 1)

RED = (1, 0.025, 0.12, 1)
BLACK = (0.025, 0.025, 0.035, 1)
WHITE = (0.95, 0.95, 0.95, 1)


class VenomApp(App):

    def build(self):
        self.start_time = None
        self.elapsed = 0

        root = BoxLayout(
            orientation="vertical",
            padding=dp(15),
            spacing=dp(12)
        )

        header = Label(
            text="[b][color=ff1238]RED VENOM[/color] [color=ffffff]V6[/color][/b]\n[color=ff334f]ULTIMATE GAMING HUB[/color]",
            markup=True,
            font_size="27sp",
            size_hint_y=None,
            height=dp(100)
        )
        root.add_widget(header)

        self.status = Label(
            text="[color=ff334f]● SYSTEM ONLINE[/color]",
            markup=True,
            size_hint_y=None,
            height=dp(30)
        )
        root.add_widget(self.status)

        scroll = ScrollView()

        content = GridLayout(
            cols=1,
            spacing=dp(12),
            size_hint_y=None
        )
        content.bind(minimum_height=content.setter("height"))

        self.stats = Label(
            text="Loading system information...",
            markup=True,
            size_hint_y=None,
            height=dp(115),
            halign="left",
            valign="middle"
        )
        self.stats.bind(
            size=lambda obj, val:
            setattr(obj, "text_size", val)
        )
        content.add_widget(self.stats)

        self.add_button(
            content, "01  CPU INFORMATION", self.cpu_info
        )
        self.add_button(
            content, "02  LIVE RAM MONITOR", self.ram_info
        )
        self.add_button(
            content, "03  STORAGE MONITOR", self.storage_info
        )
        self.add_button(
            content, "04  AIM ACCURACY TRACKER", self.aim_tracker
        )

        self.timer_label = Label(
            text="[color=ff334f]GAMING TIMER: 00:00:00[/color]",
            markup=True,
            size_hint_y=None,
            height=dp(45)
        )
        content.add_widget(self.timer_label)

        self.add_button(
            content, "05  START / STOP GAMING TIMER",
            self.toggle_timer
        )
        self.add_button(
            content, "06  DEVICE INFORMATION",
            self.device_info
        )
        self.add_button(
            content, "07  REFRESH SYSTEM",
            self.refresh_stats
        )

        scroll.add_widget(content)
        root.add_widget(scroll)

        footer = Label(
            text="[color=ff1238]R E D   V E N O M   V 6[/color]",
            markup=True,
            size_hint_y=None,
            height=dp(35)
        )
        root.add_widget(footer)

        self.refresh_stats()
        Clock.schedule_interval(self.update_timer, 1)
        Clock.schedule_interval(self.refresh_stats, 5)

        return root

    def add_button(self, parent, title, callback):
        btn = Button(
            text=title,
            size_hint_y=None,
            height=dp(58),
            background_normal="",
            background_color=(0.15, 0.015, 0.04, 1),
            color=WHITE,
            font_size="14sp",
            bold=True
        )
        btn.bind(on_press=callback)
        parent.add_widget(btn)

    def show_popup(self, title, message):
        box = BoxLayout(
            orientation="vertical",
            padding=dp(15)
        )

        label = Label(text=message, color=WHITE)

        close = Button(
            text="CLOSE",
            size_hint_y=None,
            height=dp(45),
            background_normal="",
            background_color=RED
        )

        box.add_widget(label)
        box.add_widget(close)

        popup = Popup(
            title=title,
            content=box,
            size_hint=(0.88, 0.4)
        )

        close.bind(on_press=popup.dismiss)
        popup.open()

    def cpu_info(self, *args):
        self.show_popup(
            "CPU INFORMATION",
            "Processor: " + platform.machine() +
            "\nCPU Cores: " + str(os.cpu_count())
        )

    def ram_info(self, *args):
        try:
            total = available = 0

            with open("/proc/meminfo") as f:
                for line in f:
                    if line.startswith("MemTotal:"):
                        total = int(line.split()[1])
                    elif line.startswith("MemAvailable:"):
                        available = int(line.split()[1])

            used = total - available

            msg = (
                f"Total RAM: {total // 1024} MB\n"
                f"Used RAM: {used // 1024} MB\n"
                f"Available: {available // 1024} MB"
            )

        except Exception:
            msg = "RAM information unavailable"

        self.show_popup("LIVE RAM MONITOR", msg)

    def storage_info(self, *args):
        try:
            disk = shutil.disk_usage("/")

            msg = (
                f"Total: {disk.total // 1024**3} GB\n"
                f"Used: {disk.used // 1024**3} GB\n"
                f"Free: {disk.free // 1024**3} GB"
            )

        except Exception:
            msg = "Storage information unavailable"

        self.show_popup("STORAGE MONITOR", msg)

    def aim_tracker(self, *args):
        box = BoxLayout(
            orientation="vertical",
            spacing=dp(8),
            padding=dp(12)
        )

        shots = TextInput(
            hint_text="Total shots",
            input_filter="int",
            multiline=False
        )

        heads = TextInput(
            hint_text="Headshots",
            input_filter="int",
            multiline=False
        )

        result = Label(text="Enter match statistics")

        calculate = Button(
            text="CALCULATE ACCURACY",
            background_normal="",
            background_color=RED,
            size_hint_y=None,
            height=dp(48)
        )

        box.add_widget(shots)
        box.add_widget(heads)
        box.add_widget(calculate)
        box.add_widget(result)

        popup = Popup(
            title="AIM ACCURACY TRACKER",
            content=box,
            size_hint=(0.9, 0.55)
        )

        def calculate_result(instance):
            try:
                total = int(shots.text)
                head = int(heads.text)

                if total <= 0 or head > total:
                    raise ValueError

                accuracy = head / total * 100
                result.text = f"Accuracy: {accuracy:.1f}%"

            except ValueError:
                result.text = "Enter valid shot numbers"

        calculate.bind(on_press=calculate_result)
        popup.open()

    def toggle_timer(self, *args):
        if self.start_time is None:
            self.start_time = time.time() - self.elapsed
            self.status.text = (
                "[color=ff1238]● GAMING SESSION ACTIVE[/color]"
            )
        else:
            self.elapsed = time.time() - self.start_time
            self.start_time = None
            self.status.text = "[color=ff334f]● SYSTEM ONLINE[/color]"

    def update_timer(self, dt):
        if self.start_time is not None:
            seconds = int(time.time() - self.start_time)
        else:
            seconds = int(self.elapsed)

        h, rem = divmod(seconds, 3600)
        m, s = divmod(rem, 60)

        self.timer_label.text = (
            f"[color=ff334f]GAMING TIMER: "
            f"{h:02d}:{m:02d}:{s:02d}[/color]"
        )

    def device_info(self, *args):
        self.show_popup(
            "DEVICE INFORMATION",
            f"System: {platform.system()}\n"
            f"Machine: {platform.machine()}\n"
            f"Python: {platform.python_version()}"
        )

    def refresh_stats(self, *args):
        try:
            total = available = 0

            with open("/proc/meminfo") as f:
                for line in f:
                    if line.startswith("MemTotal:"):
                        total = int(line.split()[1])
                    elif line.startswith("MemAvailable:"):
                        available = int(line.split()[1])

            used = total - available

            self.stats.text = (
                "[b][color=ff1238]SYSTEM MONITOR[/color][/b]\n"
                f"CPU Cores: {os.cpu_count()}\n"
                f"RAM Used: {used // 1024} MB\n"
                f"RAM Free: {available // 1024} MB"
            )

        except Exception:
            self.stats.text = "System monitor unavailable"


if __name__ == "__main__":
    VenomApp().run()
  
