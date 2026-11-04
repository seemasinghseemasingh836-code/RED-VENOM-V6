import os
import time
import shutil
import random

R = "\033[91m"
W = "\033[97m"
C = "\033[96m"
Z = "\033[0m"

def loading():
    print(R + "\nInitializing RED VENOM V6..." + Z)
    for i in range(1, 6):
        print(R + "Loading" + "." * i + Z)
        time.sleep(0.2)

def ram():
    try:
        with open("/proc/meminfo") as f:
            for line in f:
                if line.startswith(("MemTotal:", "MemAvailable:")):
                    print(line.strip())
    except OSError:
        print("RAM unavailable")

def storage():
    d = shutil.disk_usage(os.path.expanduser("~"))
    print("Total:", round(d.total / 1024**3, 2), "GB")
    print("Free:", round(d.free / 1024**3, 2), "GB")

def aim():
    try:
        total = int(input("Total shots: "))
        head = int(input("Headshots: "))
        if total > 0 and 0 <= head <= total:
            print("Accuracy:", round(head / total * 100, 1), "%")
        else:
            print("Invalid values")
    except ValueError:
        print("Numbers only")

def menu():
    loading()
    while True:
        os.system("clear")
        print(R + """
╔══════════════════════════════════╗
║         RED VENOM V6             ║
║       ULTIMATE GAMING HUB        ║
╠══════════════════════════════════╣
║ [1] CPU INFORMATION              ║
║ [2] RAM MONITOR                  ║
║ [3] STORAGE MONITOR              ║
║ [4] AIM ACCURACY TRACKER         ║
║ [5] GAMING SESSION TIMER         ║
║ [6] DEVICE INFORMATION           ║
║ [7] NETWORK STATUS               ║
║ [0] EXIT                         ║
╚══════════════════════════════════╝
""" + Z)

        c = input(C + "VENOM V6 >> " + Z)

        if c == "1":
            print("CPU Cores:", os.cpu_count())
        elif c == "2":
            ram()
        elif c == "3":
            storage()
        elif c == "4":
            aim()
        elif c == "5":
            start = time.time()
            input("Timer started. Press Enter to stop...")
            print("Duration:", round(time.time() - start), "seconds")
        elif c == "6":
            print("Device:", os.uname().machine)
            print("System:", os.uname().sysname)
        elif c == "7":
            print("Check Android Wi-Fi/mobile data settings.")
        elif c == "0":
            print("RED VENOM V6 CLOSED")
            break
        else:
            print("Invalid option")

        input("\nPress Enter to continue...")

if __name__ == "__main__":
    menu()
