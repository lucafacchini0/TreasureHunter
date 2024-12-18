package com.lucafacchini.entity;

import com.lucafacchini.GamePanel;
import com.lucafacchini.KeyHandler;
import com.lucafacchini.Utilities;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    // Player settings
    public final int PLAYER_HEIGHT = 19;
    public final int PLAYER_WIDTH = 11;
    public final int RESCALED_PLAYER_HEIGTH;
    public final int RESCALED_PLAYER_WIDTH;

    // Player settings
    // TODO: The player collides with objects with a "wider" Bounding Box, the more i increment the speed.
    public final int DEFAULT_PLAYER_SPEED = 5;

    // Sprite settings
    public final int UPDATE_TIME_FOR_SPRITE = 1;
    public final int MOVING_PLAYER_SPRITE_MULTIPLIER = 5;
    public final int IDLING_PLAYER_SPRITE_MULTIPLIER_DEFAULT = 20;
    public final int IDLING_PLAYER_SPRITE_MULTIPLIER_EYES_OPEN = 120;
    public final int IDLING_PLAYER_SPRITE_MULTIPLIER_EYES_CLOSED = MOVING_PLAYER_SPRITE_MULTIPLIER;

    private double spriteCounterMultiplier = MOVING_PLAYER_SPRITE_MULTIPLIER;

    public String lastPosition = "down";

    // Coordinates of the player on the screen
    public final int screenX, screenY;

    // Debug
    public int hasKey = 0;

    // Objects
    KeyHandler kh;
    Utilities utilities = new Utilities();

    public Player(GamePanel gp, KeyHandler kh) {

        super(gp);
        this.kh = kh;

        screenX = gp.SCREEN_WIDTH / 2 - gp.TILE_SIZE / 2; // Center the player on the screen
        screenY = gp.SCREEN_HEIGHT / 2 - gp.TILE_SIZE / 2; // Center the player on the screen

        // Bounding box settings
        boundingBox = new Rectangle();

        boundingBox.x = 0;
        boundingBox.y = 20;
        boundingBox.width = gp.TILE_SIZE - 20;
        boundingBox.height = gp.TILE_SIZE - 10;

        boundingBoxDefaultX = boundingBox.x;
        boundingBoxDefaultY = boundingBox.y;

        RESCALED_PLAYER_HEIGTH = PLAYER_HEIGHT * gp.SCALE;
        RESCALED_PLAYER_WIDTH = PLAYER_WIDTH * gp.SCALE;

        // Load player sprites
        setDefaultValues();
        getImages("player");
        rescaleAllPlayerImages();

    }

    void setDefaultValues() {
        worldX = gp.TILE_SIZE * 25 - gp.TILE_SIZE; // Spawn at the center of the map
        worldY = gp.TILE_SIZE * 25 - gp.TILE_SIZE; // Spawn at the center of the map

        speed = DEFAULT_PLAYER_SPEED;
        currentDirection = "down";

    }


    private void rescaleAllPlayerImages() {
        for(int i = 0; i < MAX_SPRITES_PER_WALKING_DIRECTION; i++) {
            if (upImages[i] != null) {
                upImages[i] = utilities.rescaleImage(upImages[i], RESCALED_PLAYER_WIDTH, RESCALED_PLAYER_HEIGTH);
            }
            if(downImages[i] != null) {
                downImages[i] = utilities.rescaleImage(downImages[i], RESCALED_PLAYER_WIDTH, RESCALED_PLAYER_HEIGTH);
            }
            if(leftImages[i] != null) {
                leftImages[i] = utilities.rescaleImage(leftImages[i], RESCALED_PLAYER_WIDTH, RESCALED_PLAYER_HEIGTH);
            }
            if(rightImages[i] != null) {
                rightImages[i] = utilities.rescaleImage(rightImages[i], RESCALED_PLAYER_WIDTH, RESCALED_PLAYER_HEIGTH);
            }
        }

        for(int i = 0; i < MAX_SPRITES_PER_IDLING_DIRECTION; i++) {
            if(idlingUpImages[i] != null) {
                idlingUpImages[i] = utilities.rescaleImage(idlingUpImages[i], RESCALED_PLAYER_WIDTH, RESCALED_PLAYER_HEIGTH);
            }
            if(idlingDownImages[i] != null) {
                idlingDownImages[i] = utilities.rescaleImage(idlingDownImages[i], RESCALED_PLAYER_WIDTH, RESCALED_PLAYER_HEIGTH);
            }
            if(idlingLeftImages[i] != null) {
                idlingLeftImages[i] = utilities.rescaleImage(idlingLeftImages[i], RESCALED_PLAYER_WIDTH, RESCALED_PLAYER_HEIGTH);
            }
            if(idlingRightImages[i] != null) {
                idlingRightImages[i] = utilities.rescaleImage(idlingRightImages[i], RESCALED_PLAYER_WIDTH, RESCALED_PLAYER_HEIGTH);
            }
        }
    }

    // In loop
    @Override
    public void update() {
        updateDirection();
        updateSprite();
        updatePosition();
    }

    private void updateDirection() {
        boolean isMoving = kh.isUpPressed || kh.isDownPressed || kh.isLeftPressed || kh.isRightPressed;
        boolean isIdle = !isMoving;

        if (isIdle || (kh.isUpPressed && kh.isDownPressed) || (kh.isLeftPressed && kh.isRightPressed)) {
            currentDirection = "idling-" + lastPosition;
        } else {
            if (kh.isUpPressed && kh.isLeftPressed) {
                currentDirection = "up-left";
                lastPosition = currentDirection;
            }
            else if (kh.isUpPressed && kh.isRightPressed) {
                currentDirection = "up-right";
                lastPosition = currentDirection;
            }
            else if (kh.isDownPressed && kh.isLeftPressed) {
                currentDirection = "down-left";
                lastPosition = currentDirection;
            }
            else if (kh.isDownPressed && kh.isRightPressed) {
                currentDirection = "down-right";
                lastPosition = currentDirection;
            }
            else if (kh.isUpPressed) {
                currentDirection = "up";
                lastPosition = currentDirection;
            }
            else if (kh.isDownPressed) {
                currentDirection = "down";
                lastPosition = currentDirection;
            }
            else if (kh.isLeftPressed) {
                currentDirection = "left";
                lastPosition = currentDirection;
            }
            else {
                currentDirection = "right";
                lastPosition = currentDirection;
            }
        }
    }

    private void updateSprite() {
        spriteFramesCounter++; // Increase the counter FPS times per second

        // This method has the only purpose of setting some "delays" in the sprite animation
        // depending on the current direction of the player. It does not follow any
        // logical pattern, it's just a way to make the sprite animation look better.
        setMultiplier(currentDirection, spriteImageNum);

        // Reset the sprite image number if the player is idling
        if(lastPosition.contains("idling")) {
            if(spriteImageNum > 4) { // 4 is the last sprite of the idling animation. Walk have 6.
                spriteImageNum = 1;
            }
        }

        // Update the sprite image number, depending on the spriteCounterMultiplier
        // That variable depends on the current direction of the player. It's
        // assigned in the setMultiplier method.
        if (spriteFramesCounter >= UPDATE_TIME_FOR_SPRITE * spriteCounterMultiplier) {
            spriteFramesCounter = 0;
            spriteImageNum++;
            if (spriteImageNum > MAX_SPRITES_PER_WALKING_DIRECTION) {
                spriteImageNum = 1;
            }
        }
    }

    private void updatePosition() {
        boolean isMoving = kh.isUpPressed || kh.isDownPressed || kh.isLeftPressed || kh.isRightPressed;

        if (isMoving && !(kh.isUpPressed && kh.isDownPressed) && !(kh.isLeftPressed && kh.isRightPressed)) {
            isCollidingWithTile = false;
            isCollidingWithObject = false;
            isCollidingWithEntity = false;

            gp.collisionManager.checkTile(this);
            int objectIndex = gp.collisionManager.checkObject(this, true);
            pickUpObject(objectIndex);


            // Check NPC collision
            int npcIndex = gp.collisionManager.checkEntity(this, gp.npcArray);
            interractionWithNPC(npcIndex);

            if (!isCollidingWithTile && !isCollidingWithObject && !isCollidingWithEntity) {
                movePlayer();
            } else if (isCollidingWithTile && !isCollidingWithObject && !isCollidingWithEntity) {
                handleCollision(); // TODO: Fix this method. Diagonal movement is not working while colliding with Objects.
            } else { // isCollidingWithObject
                // handleCollisionWithObject(objectIndex);
            }
        }
    }

    private void setMultiplier(String direction, int spriteImageNum) {
        if (direction.contains("idling")) {
            if (spriteImageNum == 1) {
                spriteCounterMultiplier = IDLING_PLAYER_SPRITE_MULTIPLIER_EYES_OPEN;
            } else {
                spriteCounterMultiplier = IDLING_PLAYER_SPRITE_MULTIPLIER_EYES_CLOSED;
            }
        } else {
            spriteCounterMultiplier = MOVING_PLAYER_SPRITE_MULTIPLIER;
        }
    }

    private void movePlayer() {
        if (kh.isUpPressed && kh.isLeftPressed) {
            worldY -= (int) (speed * Math.sqrt(2) / 2);
            worldX -= (int) (speed * Math.sqrt(2) / 2);
        } else if (kh.isUpPressed && kh.isRightPressed) {
            worldY -= (int) (speed * Math.sqrt(2) / 2);
            worldX += (int) (speed * Math.sqrt(2) / 2);
        } else if (kh.isDownPressed && kh.isLeftPressed) {
            worldY += (int) (speed * Math.sqrt(2) / 2);
            worldX -= (int) (speed * Math.sqrt(2) / 2);
        } else if (kh.isDownPressed && kh.isRightPressed) {
            worldY += (int) (speed * Math.sqrt(2) / 2);
            worldX += (int) (speed * Math.sqrt(2) / 2);
        } else if (kh.isUpPressed) {
            worldY -= speed;
        } else if (kh.isDownPressed) {
            worldY += speed;
        } else if (kh.isLeftPressed) {
            worldX -= speed;
        } else if (kh.isRightPressed) {
            worldX += speed;
        }
    }

    // TODO: Fix this method. Diagonal movement is not working while colliding with Objects.
    private void handleCollision() {
        if (kh.isUpPressed && kh.isLeftPressed) {
            if (gp.collisionManager.isCollidingFromLeft(this) && gp.collisionManager.isCollidingFromTop(this)) {
            } else if (gp.collisionManager.isCollidingFromLeft(this)) {
                worldY -= (int) (speed * Math.sqrt(2) / 2);
            } else if (gp.collisionManager.isCollidingFromTop(this)) {
                worldX -= (int) (speed * Math.sqrt(2) / 2);
            } else {
                worldY -= (int) (speed * Math.sqrt(2) / 2);
                worldX -= (int) (speed * Math.sqrt(2) / 2);
            }
        } else if (kh.isUpPressed && kh.isRightPressed) {
            if (gp.collisionManager.isCollidingFromRight(this) && gp.collisionManager.isCollidingFromTop(this)) {
            } else if (gp.collisionManager.isCollidingFromRight(this)) {
                worldY -= (int) (speed * Math.sqrt(2) / 2);
            } else if (gp.collisionManager.isCollidingFromTop(this)) {
                worldX += (int) (speed * Math.sqrt(2) / 2);
            } else {
                worldY -= (int) (speed * Math.sqrt(2) / 2);
                worldX += (int) (speed * Math.sqrt(2) / 2);
            }
        } else if (kh.isDownPressed && kh.isLeftPressed) {
            if (gp.collisionManager.isCollidingFromLeft(this) && gp.collisionManager.isCollidingFromBottom(this)) {
            } else if (gp.collisionManager.isCollidingFromLeft(this)) {
                worldY += (int) (speed * Math.sqrt(2) / 2);
            } else if (gp.collisionManager.isCollidingFromBottom(this)) {
                worldX -= (int) (speed * Math.sqrt(2) / 2);
            } else {
                worldY += (int) (speed * Math.sqrt(2) / 2);
                worldX -= (int) (speed * Math.sqrt(2) / 2);
            }
        } else if (kh.isDownPressed && kh.isRightPressed) {
            if (gp.collisionManager.isCollidingFromRight(this) && gp.collisionManager.isCollidingFromBottom(this)) {
            } else if (gp.collisionManager.isCollidingFromRight(this)) {
                worldY += (int) (speed * Math.sqrt(2) / 2);
            } else if (gp.collisionManager.isCollidingFromBottom(this)) {
                worldX += (int) (speed * Math.sqrt(2) / 2);
            } else {
                worldY += (int) (speed * Math.sqrt(2) / 2);
                worldX += (int) (speed * Math.sqrt(2) / 2);
            }
        }
    }

//    TODO: Fix: Check the sides of the Object. If the player is moving diagonally, check the sides of the object.
//    public void handleCollisionWithObject(int objectIndex) {
//        if (kh.isUpPressed && kh.isLeftPressed) {
//            System.out.println("UP-LEFT DETECTED");
//
//            // Check for collision with the object
//            System.out.println("NOT HITTING OBJECT BB FROM UP-LEFT" + boundingBox);
//            if (boundingBox.intersects(gp.objectsArray[objectIndex].boundingBox)) {
//                System.out.println("UP-LEFT INTERSECTION DETECTED");
//
//                // Check if the player is coming from the left
//                // Player is coming from the left if the player's right side intersects with the object's left side
//                // and there's vertical overlap.
//                if (boundingBox.getMaxX() > gp.objectsArray[objectIndex].boundingBox.getMinX() &&
//                        boundingBox.getMinX() < gp.objectsArray[objectIndex].boundingBox.getMaxX() && // The player is left of the object
//                        boundingBox.getMinY() < gp.objectsArray[objectIndex].boundingBox.getMaxY() &&
//                        boundingBox.getMaxY() > gp.objectsArray[objectIndex].boundingBox.getMinY() &&
//                        boundingBox.getMaxY() <= gp.objectsArray[objectIndex].boundingBox.getMaxY()) { // Ensure player is not hitting from below
//                    // Move up
//                    worldY -= (int) (speed * Math.sqrt(2) / 2); // Move up
//                    System.out.println("Player coming from left, moved up");
//                }
//                // Check if the player is coming from below
//                // Player is coming from below if the player's bottom side intersects with the object's top side
//                // and there's horizontal overlap.
//            }
//        }
//    }

    // TODO: Use more specific sound titles (e.g. "key_pickup", "door_open", etc.)
    private void pickUpObject(int index) {
        if(index != -1) {
            String objectName = gp.objectsArray[index].name;

            switch(objectName) {
                case "Key":
                    gp.playSound(1);
                    hasKey++;
                    gp.ui.showMessage("You picked up a key!");
                    gp.objectsArray[index] = null;
                    break;

                case "Door":
                    if(hasKey > 0) {
                        gp.ui.showMessage("You used a key!");
                        gp.playSound(3);
                        hasKey--;
                        gp.objectsArray[index] = null;
                    } else {
                        gp.ui.showMessage("You need a key to open this door!");
                    }
                    break;

                case "Boots":
                    gp.ui.showMessage("You picked up boots!");
                    gp.playSound(2);
                    speed *= 2;
                    gp.objectsArray[index] = null;
                    break;

                case "Chest":
                    gp.stopMusic();
                    gp.playSound(4);
                    gp.ui.gameFinished = true;
                    break;
            }
        }
    }

    public void interractionWithNPC(int index) {
        if(index != -1) {
            gp.gameStatus = gp.dialogueState;
            gp.npcArray[index].speak();
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        BufferedImage image;

        switch (currentDirection) {
            case "up", "up-left", "up-right" -> image = switch (spriteImageNum) {
                case 1 -> upImages[0];
                case 2 -> upImages[1];
                case 3 -> upImages[2];
                case 4 -> upImages[3];
                case 5 -> upImages[4];
                case 6 -> upImages[5];
                default -> upImages[0];
            };

            case "down", "down-left", "down-right" -> image = switch (spriteImageNum) {
                case 1 -> downImages[0];
                case 2 -> downImages[1];
                case 3 -> downImages[2];
                case 4 -> downImages[3];
                case 5 -> downImages[4];
                case 6 -> downImages[5];
                default -> downImages[0];
            };

            case "left" -> image = switch (spriteImageNum) {
                case 1 -> leftImages[0];
                case 2 -> leftImages[1];
                case 3 -> leftImages[2];
                case 4 -> leftImages[3];
                case 5 -> leftImages[4];
                case 6 -> leftImages[5];
                default -> leftImages[0];
            };

            case "right" -> image = switch (spriteImageNum) {
                case 1 -> rightImages[0];
                case 2 -> rightImages[1];
                case 3 -> rightImages[2];
                case 4 -> rightImages[3];
                case 5 -> rightImages[4];
                case 6 -> rightImages[5];
                default -> rightImages[0];
            };

            case "idling-up", "idling-up-right", "idling-up-left" -> image = switch (spriteImageNum) {
                case 1 -> idlingUpImages[0];
                case 2 -> idlingUpImages[1];
                case 3 -> idlingUpImages[2];
                case 4 -> idlingUpImages[3];
                default -> idlingUpImages[0];
            };

            case "idling-down", "idling-down-right", "idling-down-left" -> image = switch (spriteImageNum) {
                case 1 -> idlingDownImages[0];
                case 2 -> idlingDownImages[1];
                case 3 -> idlingDownImages[2];
                case 4 -> idlingDownImages[3];
                default -> idlingDownImages[0];
            };

            case "idling-left" -> image = switch (spriteImageNum) {
                case 1 -> idlingLeftImages[0];
                case 2 -> idlingLeftImages[1];
                case 3 -> idlingLeftImages[2];
                case 4 -> idlingLeftImages[3];
                default -> idlingLeftImages[0];
            };

            case "idling-right" -> image = switch (spriteImageNum) {
                case 1 -> idlingRightImages[0];
                case 2 -> idlingRightImages[1];
                case 3 -> idlingRightImages[2];
                case 4 -> idlingRightImages[3];
                default -> idlingRightImages[0];
            };

            default -> image = null;
        }

        if (image != null) {
            g2d.drawImage(image, screenX, screenY, null);

            // Debug
            g2d.setColor(Color.RED);
            g2d.drawRect(screenX + boundingBox.x, screenY + boundingBox.y, boundingBox.width, boundingBox.height);
        }
    }
}