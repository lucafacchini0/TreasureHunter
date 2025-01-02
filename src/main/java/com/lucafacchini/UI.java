package com.lucafacchini;

import com.lucafacchini.entity.Entity;

import java.awt.*;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @brief Manages the user interface of the game.
 *
 * This class handles drawing the UI elements on the screen.
 * @note: This is still experimental code, it has to be tested.
 */
public class UI {

    // Debugging
    private static final Logger LOGGER = Logger.getLogger(UI.class.getName());
    public boolean gameFinished = false;

    // Fonts
    Font dialogueFont;

    // Title screen
    public int titleScreenOption = 0;
    public int currentTitleScreenWindow = 1;

    // Dialogues
    public String currentDialogue = null;
    public String dialogueToPrint = null;
    public int currentLetter = 1;
    public boolean hasFinishedPrintingDialogue = true;

    // GamePanel instance
    GamePanel gp;

    // Graphics2D object
    Graphics2D g2d;

    // Colors
    Color dialogueWindowBackground = new Color(0, 0, 0, 150);
    Color dialogueWindowStroke = new Color(255, 255, 255);


    /**
     * Constructor of the UI class.
     *
     * @param gp the GamePanel instance.
     */
    public UI(GamePanel gp) {
        this.gp = gp;

        loadFonts();
    }






// ********************************************** DRAW METHODS ********************************************** //


    /**
     * @brief Draws the UI elements on the screen.
     * This is called every frame.
     *
     * @param g2d the Graphics2D object used to draw the elements.
     */
    public void draw(Graphics2D g2d) {
        this.g2d = g2d;

        if(gp.gameStatus == GamePanel.GameStatus.TITLE_SCREEN) {
            drawTitleScreen();
        }

        else if(gp.gameStatus == GamePanel.GameStatus.RUNNING) {
            drawEntityRelatedStuff();
        }

        else if(gp.gameStatus == GamePanel.GameStatus.DIALOGUE) {
            drawDialogues();
        }

        else if(gp.gameStatus == GamePanel.GameStatus.PAUSED) {
            // Do stuff
        }
    }






    // ********** TITLE_SCREEN STATE ********** //


    private void drawTitleScreen() {
        switch (currentTitleScreenWindow) {
            case 1 -> drawTitleScreenWindow1();
            case 2 -> drawTitleScreenWindow2();
        }
    }

    private void drawTitleScreenWindow1() {
        drawText("FacchiniRPG", Font.BOLD, 94F, Color.DARK_GRAY, Color.YELLOW, gp.TILE_SIZE * 2); // Title
        drawText("Game by Luca Facchini", Font.PLAIN, 48F, Color.DARK_GRAY, Color.WHITE, gp.TILE_SIZE * 3); // Subtitle

        // Options
        String[] options = {"PLAY GAME", "LOAD FILE", "EXIT"};

        for (int i = 0; i < options.length; i++) {
            int y = gp.TILE_SIZE * (8 + i);
            drawTitleScreenOption(options[i], y, i == titleScreenOption);
        }
    }

    private void drawTitleScreenWindow2() {
        drawText("Press enter to start the game", Font.PLAIN, 48F, null, Color.WHITE, gp.WINDOW_HEIGHT / 2);
    }

    private void drawText(String text, int fontStyle, float fontSize, Color shadowColor, Color textColor, int y) {
        g2d.setFont(dialogueFont.deriveFont(fontStyle, fontSize)); // Use dialogueFont explicitly
        int x = getCenteredX(text);

        if (shadowColor != null) {
            g2d.setColor(shadowColor);
            g2d.drawString(text, x + 3, y + 3);
        }

        g2d.setColor(textColor);
        g2d.drawString(text, x, y);
    }

    private void drawTitleScreenOption(String text, int y, boolean isSelected) {
        g2d.setFont(dialogueFont.deriveFont(Font.BOLD, 48F));
        int x = getCenteredX(text);

        g2d.setColor(Color.WHITE);
        g2d.drawString(text, x, y);

        if (isSelected) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString(">", x - gp.TILE_SIZE, y);
        }
    }






    // ********** RUNNING STATE ********** //


    /**
     * @brief Draws a bulb above the entity.
     *
     * @param entity the entity to draw the bulb above.
     */
    private void drawBulb(Entity entity) {
        int x, y, width, height;

        width = gp.TILE_SIZE / 4;
        height = gp.TILE_SIZE / 4;
        x = worldToScreenX(entity.worldX) + gp.TILE_SIZE / 2 - width / 2;
        y = worldToScreenY(entity.worldY) - gp.TILE_SIZE / 2;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, width, height);
    }





    // ********** DIALOGUE STATE ********** //



    /**
     * @brief Draws the dialogue screen.
     *
     * It starts by calculating the x, y, width, and height of the dialogue screen.
     * Then it draws the sub window and the dialogue text.
     */
    private void drawDialogueScreen() {
        g2d.setFont(dialogueFont);

        int x, y, width, height;

        x = gp.TILE_SIZE * 2;
        y = gp.TILE_SIZE;
        width = gp.WINDOW_WIDTH - gp.TILE_SIZE * 4;
        height = gp.TILE_SIZE * 4;

        drawDialogueWindow(x, y, width, height);
        drawDialogueString(x, y, currentDialogue);
    }


    /**
     * @brief Draws a sub window on the screen. (the background)
     *
     * @param x the x coordinate of the window.
     * @param y the y coordinate of the window.
     * @param width the width of the window.
     * @param height the height of the window.
     *
     */
    private void drawDialogueWindow(int x, int y, int width, int height) {
        g2d.setColor(dialogueWindowBackground);
        g2d.fillRoundRect(x, y, width, height, 50, 50);

        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(dialogueWindowStroke);
        g2d.drawRoundRect(x+5, y+5, width-10, height-10, 50, 50);
    }


    /**
     * @brief Draws a dialogue string on the screen.
     *
     * It starts by setting the font size and style.
     * Then it draws the dialogue string on the screen.
     *
     * @param x the x coordinate of the string.
     * @param y the y coordinate of the string.
     * @param dialogue the string to draw.
     */
    private void drawDialogueString(int x, int y, String dialogue) {
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 36F));

        x += gp.TILE_SIZE;
        y += gp.TILE_SIZE;

        if(isPlayerReadyForNextDialogue()) {
            hasFinishedPrintingDialogue = false;
            currentLetter = 1;
        }

        if (currentLetter > dialogue.length()) {
            hasFinishedPrintingDialogue = true;
            currentLetter = dialogue.length();
        }

        dialogueToPrint = dialogue.substring(0, currentLetter);

        g2d.setColor(Color.BLACK);
        g2d.drawString(dialogueToPrint, x + 2, y + 2);
        g2d.setColor(Color.WHITE);
        g2d.drawString(dialogueToPrint, x, y);

        currentLetter++;
    }







    // ********** GENERAL METHODS (ANY GAME STATE) ********** //


    /**
     * @brief Shows a message on the screen.
     *
     * @note: This method is still experimental.
     *
     * @param text the text to show on the screen.
     */
    public void showMessage(String text) {
        g2d.setFont(dialogueFont);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, 100, 100);
    }





// ********************************************** HELPER METHODS ********************************************** //


    private void loadFonts() {
        try {
            InputStream is = Objects.requireNonNull(getClass().getResourceAsStream("/fonts/Pixel-Life.ttf"), "Font resource not found");
            dialogueFont = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            LOGGER.severe("Error loading font: " + e.getMessage());
        }
    }

    private void drawEntityRelatedStuff() {
        for(Entity entity : gp.npcArray) {
            if(entity != null && entity.isNextToPlayer) {
                drawBulb(entity);
            }
        }
    }

    private void drawDialogues() {
        drawDialogueScreen();
    }

    private int worldToScreenX(int worldX) {
        return worldX - gp.player.worldX + gp.player.screenX;
    }

    private int worldToScreenY(int worldY) {
        return worldY - gp.player.worldY + gp.player.screenY;
    }

    private boolean isPlayerReadyForNextDialogue() {
        return gp.player.isReadyForNextDialogue;
    }

    private int getCenteredX(String text) {
        int length = (int)g2d.getFontMetrics().getStringBounds(text, g2d).getWidth();

        return (gp.WINDOW_WIDTH - length) / 2;
    }
}