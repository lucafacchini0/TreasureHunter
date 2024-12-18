package com.lucafacchini.objects;

import com.lucafacchini.GamePanel;
import com.lucafacchini.Utilities;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SuperObject {
    // Coordinates
    public int worldX, worldY;
    public int screenX, screenY;

    // TODO: Implement a constructor to construct every object faster and resize it automatically.

    // Properties
    public boolean isSolid = false;

    // Objects
    public BufferedImage image;
    public String name;
    public Rectangle boundingBox = new Rectangle(0, 0, 64, 64); // TODO: Replace size with gp.TILE_SIZE
    public Utilities utilities = new Utilities();

    public int boundingBoxDefaultX = 0;
    public int boundingBoxDefaultY = 0;

    public void draw(Graphics2D g2d, GamePanel gp) {
        screenX = worldX - gp.player.worldX + gp.player.screenX;
        screenY = worldY - gp.player.worldY + gp.player.screenY;

        // If the object is within the screen boundaries, draw it.
        if (worldX + gp.TILE_SIZE > gp.player.worldX - gp.player.screenX &&
                worldX - gp.TILE_SIZE < gp.player.worldX + gp.player.screenX &&
                worldY + gp.TILE_SIZE > gp.player.worldY - gp.player.screenY &&
                worldY - gp.TILE_SIZE < gp.player.worldY + gp.player.screenY) {
            g2d.drawImage(image, screenX, screenY, null);
        }

        //Debug ##IMPORTANT
        g2d.setColor(Color.BLACK);
        g2d.drawRect(screenX + boundingBox.x, screenY + boundingBox.y, boundingBox.width, boundingBox.height);
    }
}