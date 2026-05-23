# 🪨 ThaiChainMiner

**ปลั๊กอินระบบขุดแร่สายสำหรับ Minecraft 1.20+ (Spigot/Paper)**

---

## 📋 ฟีเจอร์

### ⛏️ ระบบขุดแร่ยกสาย (Vein Miner)
- ขุดแร่ทั้งสาย (ทั้ง 19 ชนิด) เมื่อเชื่อมต่อกัน
- บล็อกแตกทยอยเป็นระลอก (Cascade Logic) ไม่ใช่หายไปพร้อมกัน
- ปรับจำนวนสูงสุดแยกตามแร่ได้ใน `config.yml`

### 🪨 ระบบขุดหิน (Stone Miner)
- ขุดหิน 4 บล็อกพร้อมกัน
- ระบบป้องกันการทุบสิ่งก่อสร้าง (Anti-Grief Safety)
- เฉพาะเมื่อมี 10 บล็อกขึ้นไปในสายเดียว

### 🔊 ระบบเสียงขั้นสูง (Advanced Sound Design)
- เล่นเสียงที่ทุกบล็อกที่แตก
- เสียงจำลอง (Sound Double Simulation)
- สุ่ม Pitch และปรับ Volume ตามจำนวนบล็อก

### 💪 ระบบความทนทาน (Durability)
- คำนวณตามจำนวนบล็อกที่แตกจริง
- รองรับเอนแชนต์ Unbreaking
- ยกเลิกดรอปเมื่อเนื้อที่ขุดไม่เพียงพอ

### 🛡️ ระบบตรวจสอบเทียร์ (Tier Requirement)
- ตรวจสอบตามระดับ Bedrock Edition
- ไม่ได้ drop แร่ถ้าที่ขุดไม่ถึงระดับ

### ⚙️ ระบบคำสั่ง (Commands)
- `/mine on` - เปิดระบบในโลกปัจจุบัน
- `/mine off` - ปิดระบบในโลกปัจจุบัน
- `/mine list` - แสดงโลกที่เปิดใช้งาน
- `/mine reload` - รีโหลด config
- `/mine ban <ผู้เล่น>` - บล็อกผู้เล่น
- `/mine unban <ผู้เล่น>` - ปลดบล็อก
- `/mine banlist` - แสดงรายชื่อแบน
- `/mine help` - แสดงวิธีใช้

---

## 📦 การติดตั้ง

### ข้อกำหนด
- Java 17 ขึ้นไป
- Spigot/Paper 1.20.1+
- Maven (สำหรับคอมไพล์)

### วิธีคอมไพล์

```bash
# โคลน Repository
git clone https://github.com/rov1thai999-cell/ThaiChainMiner.git
cd ThaiChainMiner

# คอมไพล์
mvn clean package

# ไฟล์ .jar อยู่ที่: target/ThaiChainMiner-1.0.0.jar
```

### วิธีติดตั้ง

1. คัดลอก `ThaiChainMiner-1.0.0.jar` ไปยังโฟลเดอร์ `plugins` ของเซิร์ฟเวอร์
2. รีสตาร์ทเซิร์ฟเวอร์
3. ไฟล์ config จะสร้างอัตโนมัติใน `plugins/ThaiChainMiner/`

---

## ⚙️ การตั้งค่า (config.yml)

### เปิด/ปิดโลก
```yaml
enabled-worlds:
  - world
  - world_nether
```

### ตั้งค่าแร่
```yaml
vein-miner:
  coal_ore:
    enabled: true
    max-blocks: 64
```

### ตั้งค่าความเสียหายของที่ขุด
```yaml
pickaxe-damage:
  iron_pickaxe:
    damage-per-block: 1
```

### ตั้งค่าเสียง
```yaml
sound:
  enabled: true
  double-sound-simulation: true
  sound-delay: 100
```

---

## 🎮 วิธีใช้

1. **ขุดแร่ยกสาย**: คลิกขวาบล็อกแร่ด้วยที่ขุด
2. **ขุดหิน**: คลิกขวาหิน จะขุด 4 บล็อก (ถ้ามี 10 บล็อกขึ้นไป)
3. **บล็อกผู้เล่น**: `/mine ban <ชื่อผู้เล่น>`
4. **ปลดบล็อก**: `/mine unban <ชื่อผู้เล่น>`

---

## 🔐 Permissions

- `thaichainminer.command.mine` - ใช้คำสั่ง /mine
- `thaichainminer.use` - ใช้งานระบบขุดแร่สาย (ค่าเริ่มต้น: true)

---

## 🐛 การแก้ไขปัญหา

### ไม่ขุดแร่สาย
- ตรวจสอบว่าแร่เปิดใช้งานใน config.yml
- ตรวจสอบว่าที่ขุดมีเทียร์พอเพียง
- ตรวจสอบว่าโลกเปิดใช้งาน (`/mine list`)

### เสียงไม่ได้ยิน
- ตรวจสอบ `sound.enabled: true` ใน config
- ตรวจสอบระดับเสียงเกม

---

## 📝 License

MIT License - ใช้งานได้อย่างอิสระ

---

## 🙏 ขอบคุณ

ทำให้ Minecraft สนุกยิ่งขึ้น! ⛏️✨