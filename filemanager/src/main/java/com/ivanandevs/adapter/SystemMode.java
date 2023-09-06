package com.ivanandevs.adapter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class SystemMode {
    private long RamTotalSize;
    private long RamUsableSize;
    private long RomTotalSize;
    private long RomUsableSize;

    public long getRamTotalSize() {
        return this.RamTotalSize;
    }

    public void setRamTotalSize(long j) {
        this.RamTotalSize = j;
    }

    public long getRamUsableSize() {
        return this.RamUsableSize;
    }

    public void setRamUsableSize(long j) {
        this.RamUsableSize = j;
    }

    public long getRomTotalSize() {
        return this.RomTotalSize;
    }

    public long getRomUsableSize() {
        return this.RomUsableSize;
    }

    public String getUnSize() {
        return fileSizeByteToM(Long.valueOf(this.RomTotalSize - this.RomUsableSize));
    }

    public String getRAMUnSize() {
        return fileSizeByteToM(Long.valueOf(this.RamTotalSize - this.RamUsableSize));
    }

    public String getTotalSize() {
        return fileSizeByteToM(Long.valueOf(this.RomTotalSize));
    }

    public String getRamTSize() {
        return fileSizeByteToM(Long.valueOf(this.RamTotalSize));
    }

    public void setRomTotalSize(long j) {
        this.RomTotalSize = j;
    }

    public String getUsableSize() {
        return fileSizeByteToM(Long.valueOf(this.RomUsableSize));
    }

    public void setRomUsableSize(long j) {
        this.RomUsableSize = j;
    }

    public int getSROM() {
        return (int) (new BigDecimal(Double.toString((double) (getRomTotalSize() - getRomUsableSize()))).divide(new BigDecimal(Double.toString((double) getRomTotalSize())), 2, 4).doubleValue() * 100.0d);
    }

    public int getSRAM() {
        return (int) (new BigDecimal(Double.toString((double) (getRamTotalSize() - getRamUsableSize()))).divide(new BigDecimal(Double.toString((double) getRamTotalSize())), 2, 4).doubleValue() * 100.0d);
    }

    public static int getFileSizeB(Long l) {
        return (int) (l.longValue() / 5000);
    }

    public static String[] getFileSize(Long l) {
        BigDecimal bigDecimal = new BigDecimal(l.longValue());
        BigDecimal bigDecimal2 = new BigDecimal(1000);
        int i = 0;
        while (bigDecimal.compareTo(bigDecimal2) > 0 && i < 5) {
            bigDecimal = bigDecimal.divide(bigDecimal2);
            i++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        StringBuilder sb = new StringBuilder();
        sb.append(decimalFormat.format(bigDecimal));
        String str = "";
        sb.append(str);
        String sb2 = sb.toString();
        if (i == 0) {
            str = "B";
        } else if (i == 1) {
            str = "KB";
        } else if (i == 2) {
            str = "MB";
        } else if (i == 3) {
            str = "GB";
        } else if (i == 4) {
            str = "TB";
        } else if (i == 5) {
            str = "PB";
        }
        return new String[]{sb2, str};
    }

    public static String fileSizeByteToM(Long l) {
        BigDecimal bigDecimal = new BigDecimal(l.longValue());
        BigDecimal bigDecimal2 = new BigDecimal(1000);
        int i = 0;
        while (bigDecimal.compareTo(bigDecimal2) > 0 && i < 5) {
            bigDecimal = bigDecimal.divide(bigDecimal2);
            i++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String str = decimalFormat.format(bigDecimal) + "";
        if (i == 0) {
            return str + "B";
        } else if (i == 1) {
            return str + "KB";
        } else if (i == 2) {
            return str + "MB";
        } else if (i == 3) {
            return str + "GB";
        } else if (i == 4) {
            return str + "TB";
        } else if (i != 5) {
            return str;
        } else {
            return str + "PB";
        }
    }
}
