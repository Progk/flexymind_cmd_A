package com.example.forestgame.gameinterface;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import android.util.Log;

import com.example.forestgame.GameScene;
import com.example.forestgame.MainActivity;
import com.example.forestgame.SlotMatrix;
import com.example.forestgame.element.Element;
import com.example.forestgame.element.TableOfElements;

public class Prison extends GameSlot {
    
    private final static float PRISON_POSITION_LEFT = MainActivity.TEXTURE_WIDTH * 136 / 625;
    private final static float PRISON_POSITION_UP = MainActivity.TEXTURE_HEIGHT * 1381 / 2000;
    private final static float PRISON_WIDTH = MainActivity.TEXTURE_WIDTH * 61 / 250;
    private final static float PRISON_HEIGHT = MainActivity.TEXTURE_HEIGHT * 303 / 2000;
    private static final int PRISON_Z_INDEX = 401;
    private static float touchPointX;
    private static float touchPointY;
    
    public Prison(GameScene scene) {
	
	super(scene);
    }
    
    protected void backToGameSlot(Element element) {
	
	slotSprite.setPosition( PRISON_POSITION_LEFT
    		                , PRISON_POSITION_UP);
	
    }
    
    protected void show() {
	
	if(!isEmpty) {
	    
	    MainActivity.mainActivity.mStep.play();
	    
	    slotTexture = MainActivity.mainActivity.storage.getTexture(TableOfElements.getTextureName(element));
	    slotSprite = new Sprite ( PRISON_POSITION_LEFT
		    		      , PRISON_POSITION_UP
		    		      , PRISON_WIDTH
		    		      , PRISON_HEIGHT
		    		      , slotTexture
		    		      , MainActivity.mainActivity.getVertexBufferObjectManager()) {
		
		@Override
		public boolean onAreaTouched( TouchEvent pSceneTouchEvent
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
	
	    gameSlotAttach(PRISON_Z_INDEX);
	    
	} else {
	    
	    gameSlotDetach();
	}
    }
    
    protected void gameSlotIsActionDown() {
	
	gameScene.attachHelpForElement(element);
	Log.d("prison", "touch");
	row = SlotMatrix.getPrisonPlaceRow();
	column = SlotMatrix.getPrisonPlaceColumn();
	slotSprite.registerEntityModifier(rotationModifier);
    }
    
    protected void gameSlotIsActionUp() {
	
	gameScene.detachHelpForElement();
	slotSprite.unregisterEntityModifier(rotationModifier);
	gameScene.moveElement(touchPointX, touchPointY - VERTICAL_OFFSET);
	column = gameScene.getPutInColumn();
	row = gameScene.getPutInRow(); 
	Log.d("prison", Integer.toString(row));
	Log.d("prison", Integer.toString(column));
        gameScene.detachChild(gameScene.getBacklight());


        if (row < SlotMatrix.getROWS() && column < SlotMatrix.getCOLUMNS() 
		&& ((gameScene.getSlotMatrix().isSlotEmpty(row, column) 
			&& !element.getName().equals("MAGIC_STICK"))
		|| (!gameScene.getSlotMatrix().isSlotEmpty(row, column)
			&& element.getName().equals("MAGIC_STICK")))) {

	    Log.d("prison", "new");
	    gameScene.getSlotMatrix().putToSlot(element, row, column);
	    clear();

	} else {

	    Log.d("prison","nowhere");
	    backToGameSlot(element);
	}
    }
    
    protected void gameSlotIsActionMove(TouchEvent pSceneTouchEvent) {

	Log.d("prison", "move");

	touchPointX = pSceneTouchEvent.getX();
	touchPointY = pSceneTouchEvent.getY();
	float spriteLeftBorder = touchPointX - slotSprite.getWidth() / 2;
	float spriteUpBorder = touchPointY - slotSprite.getHeight() / 2 - VERTICAL_OFFSET;
	slotSprite.setPosition(spriteLeftBorder, spriteUpBorder);
	boolean elementIsMagicStick = element.getName().equals("MAGIC_STICK");
	gameScene.backLight(touchPointX, touchPointY, elementIsMagicStick);     
    }
}
