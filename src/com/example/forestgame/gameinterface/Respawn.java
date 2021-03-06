package com.example.forestgame.gameinterface;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import android.util.Log;

import com.example.forestgame.GameScene;
import com.example.forestgame.MainActivity;
import com.example.forestgame.SlotMatrix;
import com.example.forestgame.element.Element;
import com.example.forestgame.element.TableOfElements;

public class Respawn extends GameSlot {
    
    private final static float RESPAWN_POSITION_LEFT = MainActivity.TEXTURE_WIDTH * 27 / 50;
    private final static float RESPAWN_POSITION_UP = MainActivity.TEXTURE_HEIGHT * 1381 / 2000;
    private static float touchPointX;
    private static float touchPointY;
    
    public Respawn(GameScene scene) {
	
	super(scene);
	generateElement();
    }
    
    public void generateElement() {
	
	element = TableOfElements.getRandomElement();
	isEmpty = false;
	show();
    }

    //For Animation
    protected void backToGameSlot(Element e) {
	
	slotSprite.setPosition(RESPAWN_POSITION_LEFT
				  , RESPAWN_POSITION_UP);
	
    }
    
    protected void show() {
	
	if(!isEmpty) {
	    
	    slotTexture = MainActivity.mainActivity.storage.getTexture(TableOfElements
			    						  . getTextureName
			    						  (element));
	    
	    
	    
	    slotSprite = new Sprite ( RESPAWN_POSITION_LEFT
		    			 , RESPAWN_POSITION_UP
		    			 , GAME_SLOT_WIDTH
		    			 , GAME_SLOT_HEIGHT
		    			 , slotTexture
		    			 , MainActivity.mainActivity.getVertexBufferObjectManager()) {
		
		@Override
		public boolean onAreaTouched(TouchEvent pSceneTouchEvent
			    		     , float pTouchAreaLocalX
			    		     , float pTouchAreaLocalY) {

		    if (pSceneTouchEvent.isActionDown()) {
			
			gameSlotIsActionDown();
			    
		    } else if (pSceneTouchEvent.isActionUp()) {
			
			gameSlotIsActionUp();
			
		    } else if (pSceneTouchEvent.isActionMove()) {
			    
			gameSlotIsActionMove(pSceneTouchEvent);
			
		    }
		    return true;
		}
	    };
	    
	    gameSlotAttach(GAME_SLOT_Z_INDEX);
	    
	} else {
	    
	    gameSlotDetach();
	}
    }
    
    protected void gameSlotIsActionDown() {
	
	gameScene.attachHelpForElement(element);
	row = SlotMatrix.getRespawnPlaceRow();
	column = SlotMatrix.getRespawnPlaceColumn();
	slotSprite.registerEntityModifier(rotationModifier);
	Log.d("resp", "touch");
	Log.d("resp", Integer.toString(row));
	Log.d("resp", Integer.toString(column));
    }
    
    protected void gameSlotIsActionUp() {
	
	gameScene.detachHelpForElement();
	slotSprite.unregisterEntityModifier(rotationModifier);
	Log.d("resp", "no touch");
	Log.d("resp", Integer.toString(row));
	Log.d("resp", Integer.toString(column));
	
	gameScene.moveElement(touchPointX, touchPointY - VERTICAL_OFFSET);
	column = gameScene.getPutInColumn();
	row = gameScene.getPutInRow(); 
	Log.d("resp", Integer.toString(row));
	Log.d("resp", Integer.toString(column));
        gameScene.detachChild(gameScene.getBacklight());
        
	
	if (column == SlotMatrix.getPrisonPlaceColumn() && row  == SlotMatrix.getPrisonPlaceRow() 
		&& gameScene.getPrison().isEmpty()) {
	    
	    Log.d("resp", "newprison");
	    gameScene.getPrison().addElement(element);
	    clear();
	    generateElement();
	    
	} else if (row < SlotMatrix.getROWS() && column < SlotMatrix.getCOLUMNS() 
		&& ((gameScene.getSlotMatrix().isSlotEmpty(row, column) 
			&& !element.getName().equals("MAGIC_STICK"))
		|| (!gameScene.getSlotMatrix().isSlotEmpty(row, column)
			&& element.getName().equals("MAGIC_STICK")))) {
	    
	    Log.d("resp", "newSlot");
	    gameScene.getSlotMatrix().putToSlot(element, row, column);
	    clear();
	    generateElement();
	    
	} else {
	    
	    Log.d("resp", Integer.toString(row));
	    Log.d("resp", Integer.toString(column));
	    Log.d("resp","nowhere");
	    backToGameSlot(element);
	}
	touchPointX = RESPAWN_POSITION_LEFT+200;
	touchPointY = RESPAWN_POSITION_UP+200;
	
    }
    
    protected void gameSlotIsActionMove(TouchEvent pSceneTouchEvent) {

	Log.d("resp", "move");
	touchPointX = pSceneTouchEvent.getX();
	touchPointY = pSceneTouchEvent.getY();
	float spriteLeftBorder = touchPointX - slotSprite.getWidth() / 2;
	float spriteUpBorder = touchPointY - slotSprite.getHeight() / 2 - VERTICAL_OFFSET;
	slotSprite.setPosition(spriteLeftBorder, spriteUpBorder);
	gameScene.backLight(touchPointX, touchPointY - VERTICAL_OFFSET, element.getName());
    }
}
