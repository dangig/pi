package com.summerframework.pi.i2c;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/*
 * Controls the OLED 0.96", SSD1306 by I2C, 128x64.
 * 
 * Sources: https://github.com/ondryaso/pi-ssd1306-java/blob/master/src/eu/ondryaso/ssd1306/Display.java
 * https://github.com/squix78/esp8266-oled-ssd1306/blob/master/SSD1306.cpp
 */
public class SSD1306 {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSD1306.class);

	public final static int SSD1306_ADDRESS = 0x3C;

	public static final int BLACK = 0;
	public static final int WHITE = 1;
	public static final int INVERSE = 2;

	public static final int width_POS = 0;
	public static final int height_POS = 1;
	public static final int FIRST_CHAR_POS = 2;
	public static final int CHAR_NUM_POS = 3;
	public static final int CHAR_width_START_POS = 4;

	public static final int TEXT_ALIGN_LEFT = 0;
	public static final int TEXT_ALIGN_CENTER = 1;
	public static final int TEXT_ALIGN_RIGHT = 2;

	public static final int CHARGEPUMP = 0x8D;
	public static final int COLUMNADDR = 0x21;
	public static final int COMSCANDEC = 0xC8;
	public static final int COMSCANINC = 0xC0;
	public static final int DISPLAYALLON = 0xA5;
	public static final int DISPLAYALLON_RESUME = 0xA4;
	public static final int DISPLAYOFF = 0xAE;
	public static final int DISPLAYON = 0xAF;
	public static final int EXTERNALVCC = 0x1;
	public static final int INVERTDISPLAY = 0xA7;
	public static final int MEMORYMODE = 0x20;
	public static final int NORMALDISPLAY = 0xA6;
	public static final int PAGEADDR = 0x22;
	public static final int SEGREMAP = 0xA0;
	public static final int SETCOMPINS = 0xDA;
	public static final int SETCONTRAST = 0x81;
	public static final int SETDISPLAYCLOCKDIV = 0xD5;
	public static final int SETDISPLAYOFFSET = 0xD3;
	public static final int SETHIGHCOLUMN = 0x10;
	public static final int SETLOWCOLUMN = 0x00;
	public static final int SETMULTIPLEX = 0xA8;
	public static final int SETPRECHARGE = 0xD9;
	public static final int SETSEGMENTREMAP = 0xA1;
	public static final int SETSTARTLINE = 0x40;
	public static final int SETVCOMDETECT = 0xDB;
	public static final int SWITCHCAPVCC = 0x2;

	private static boolean verbose = true;

	private I2CBus bus;
	private I2CDevice ssd1306;

	private int width;
	private int height;
	private int pages;

	private BufferedImage img;
	private Graphics2D graphics;
	private byte[] buffer;

	public SSD1306() {
		this(SSD1306_ADDRESS);
	}

	public SSD1306(int address) {
		try {
			// Get i2c bus
			bus = I2CFactory.getInstance(I2CBus.BUS_1); // Depends onthe RasPI
														// version
			if (verbose)
				LOGGER.debug("Connected to bus. OK.");

			// Get device itself
			ssd1306 = bus.getDevice(address);
			if (verbose)
				LOGGER.debug("Connected to device. OK!!!!!");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		this.width = 128;
		this.height = 64;
		this.pages = 8;
		this.img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		this.graphics = this.img.createGraphics();
	}

	/**
	 * Works with either 0x00 and 0x80... Using 0x00 for now.
	 * 
	 * @param command
	 */
	public void sendCommand(int command) {
		try {
			ssd1306.write(0x00, (byte) command);
		} catch (IOException e) {
			LOGGER.error("An error occurred sending command " + command, e);
		}
	}

	public void sendData(byte[] data) {
		try {
			ssd1306.write(0x40, data, 0, data.length);
		} catch (IOException e) {
			LOGGER.error("An error occurred sending data.", e);
		}
	}

	public void sendInitCommands() {
		try {
			if (verbose) {
				LOGGER.debug("Begin sequence started... (from this device tutorial)");
			}

			sendCommand(DISPLAYOFF);
			sendCommand(NORMALDISPLAY);
			sendCommand(SETDISPLAYCLOCKDIV);
			sendCommand(0x80);
			sendCommand(SETMULTIPLEX);
			sendCommand(0x3F);
			sendCommand(SETDISPLAYOFFSET);
			sendCommand(0x00);
			sendCommand(SETSTARTLINE | 0x00);
			sendCommand(CHARGEPUMP);
			sendCommand(0x14);
			sendCommand(MEMORYMODE);
			sendCommand(0x00);
			sendCommand(SEGREMAP);
			sendCommand(COMSCANINC);
			sendCommand(SETCOMPINS);
			sendCommand(0x12);
			sendCommand(SETCONTRAST);
			sendCommand(0xCF);
			sendCommand(SETPRECHARGE);
			sendCommand(0xF1);
			sendCommand(SETVCOMDETECT);
			sendCommand(0x40);
			sendCommand(DISPLAYALLON_RESUME);
			sendCommand(NORMALDISPLAY);
			sendCommand(0x2e); // stop scroll
			sendCommand(DISPLAYON);

			if (verbose) {
				LOGGER.debug("Init sequence completed.");
			}
		} catch (Exception ex) {
			System.err.println("Begin:" + ex.toString());
		}
	}

	public void begin() {
		try {
			if (verbose) {
				LOGGER.debug("begin started... (from this device tutorial)");
			}

			sendInitCommands();
			sendCommand((byte) DISPLAYON);
			clear();
			display();

			if (verbose) {
				LOGGER.debug("begin completed.");
			}
		} catch (Exception ex) {
			System.err.println("Begin:" + ex.toString());
		}
	}

	// public void reset() throws Exception {
	// // No reset...
	// }

	public void close() {
		try {
			this.bus.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Sends the buffer to the display
	 */
	public synchronized void display() {
		try {
			sendCommand((byte) COLUMNADDR);
			sendCommand((byte) 0x0);
			sendCommand((byte) 0x7F); // 127

			sendCommand((byte) PAGEADDR);
			sendCommand((byte) 0x0);
			sendCommand((byte) 0x7);

			sendData(buffer);

		} catch (Exception e) {
			LOGGER.error("An error occurred", e);
		}

	}

	/**
	 * Clears the buffer
	 */
	public void clear() {
		this.buffer = new byte[this.width * this.pages];
	}

	/**
	 * Sets one pixel in the current buffer
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 * @param white
	 *            White or black pixel
	 * @return True if the pixel was successfully set
	 */
	public boolean setPixel(int x, int y, boolean white) {
		if (x < 0 || x > this.width || y < 0 || y > this.height) {
			return false;
		}

		if (white) {
			this.buffer[x + (y / 8) * this.width] |= (1 << (y & 7));
		} else {
			this.buffer[x + (y / 8) * this.width] &= ~(1 << (y & 7));
		}

		return true;
	}

	/**
	 * Copies AWT image contents to buffer. Calls display()
	 * 
	 * @see Display#display()
	 */
	public synchronized void displayImage() {
		Raster r = this.img.getRaster();

		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				this.setPixel(x, y, (r.getSample(x, y, 0) > 0));
			}
		}

		this.display();
	}

	public void invertDisplay(boolean invert) {

		LOGGER.debug("Inverting display: " + invert);
		try {

			if (invert) {
				sendCommand((byte) INVERTDISPLAY);
			} else {
				sendCommand((byte) NORMALDISPLAY);
			}

		} catch (Exception e) {
			LOGGER.error("Error occurred in invertDisplay", e);
		}
	}

	protected static void waitfor(long howMuch) {
		try {
			Thread.sleep(howMuch);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

	public BufferedImage getImage() {
		return this.img;
	}

	public Graphics2D getGraphics() {
		return this.graphics;
	}

	public static void mainX(String[] args) {
		SSD1306 ssd1306 = new SSD1306();

		try {
			ssd1306.begin();
		} catch (Exception e) {
			LOGGER.error("An error occurred", e);
			System.exit(1);
		}

		try {

			// Image i = ImageIO.

			ssd1306.getGraphics().setColor(Color.WHITE);
			ssd1306.getGraphics().setFont(new Font("Monospaced", Font.PLAIN, 12));
			// ssd1306.getGraphics().drawString("Test!!!", 60, 30);
			ssd1306.getGraphics().drawRect(0, 0, 127, 63);
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
			ssd1306.getGraphics().drawString(sdf.format(new Date()), 9, 20);

			ssd1306.getGraphics().setFont(new Font("Monospaced", Font.PLAIN, 16));
			ssd1306.getGraphics().drawLine(0, 32, 127, 32);
			ssd1306.getGraphics().drawLine(63, 32, 63, 63);

			ssd1306.getGraphics().drawString("21.5 C", 2, 55);
			ssd1306.getGraphics().drawString("33.2 %", 66, 55);

			ssd1306.displayImage();

		} catch (Exception e) {
			LOGGER.error("An error occurred", e);
		}

	}
}