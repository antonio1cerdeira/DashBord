package cenario;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Vector3f;

public class KeyControl extends Behavior {
	private TransformGroup moveTg;

	private Node node;
	private WakeupCondition wakeupCondition = null;
	private boolean collision = false;
	int lastKey;

	
	public KeyControl(TransformGroup tg, Node n) {
		moveTg = tg;
		node = n;
	
	}

	@Override
	public void initialize() {

		WakeupCriterion[] events = new WakeupCriterion[4];
		events[0] = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
		events[1] = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
		/*  if (node != null) {
		        events[2] = new WakeupOnCollisionExit(node, WakeupOnCollisionExit.USE_GEOMETRY);
		    }*/
		  
		events[2] = new WakeupOnCollisionEntry(node, WakeupOnCollisionEntry.USE_GEOMETRY);
		events[3] = new WakeupOnCollisionExit(node, WakeupOnCollisionExit.USE_GEOMETRY);

		wakeupCondition = new WakeupOr(events);

		wakeupOn(wakeupCondition);

	}

	@Override
	public void processStimulus(Enumeration criteria) {
		WakeupCriterion wakeupCriterion;
		AWTEvent[] events;
		while (criteria.hasMoreElements()) {
			wakeupCriterion = (WakeupCriterion) criteria.nextElement();
			if (wakeupCriterion instanceof WakeupOnAWTEvent) {
				events = ((WakeupOnAWTEvent) wakeupCriterion).getAWTEvent();

				for (int i = 0; i < events.length; i++) {
					if (events[i].getID() == KeyEvent.KEY_PRESSED) {
						keyPressed((KeyEvent) events[i]);
					} else {
						
					}
				}
			} else if (wakeupCriterion instanceof WakeupOnCollisionEntry) {
				collision = true;

			} else if (wakeupCriterion instanceof WakeupOnCollisionExit) {
				collision = false;

			}

		}

		wakeupOn(wakeupCondition);

	}
	 private float  translationAmount = 0.01f;
	 private float rotationAmount = (float) Math.toRadians(1.0);
	private int consecutiveContraryKeyPressCount = 0;
	private void keyPressed(KeyEvent event) {
		
		int keycode = event.getKeyCode();
		 if (collision && lastKey != -1) {
		        translationAmount = 0.1f;  
		        rotationAmount = (float) Math.toRadians(5.0);  

		        if (isOppositeKey(keycode, lastKey)) {
		            consecutiveContraryKeyPressCount++;

		            if (consecutiveContraryKeyPressCount < 2) {
		                return;
		            } else {
		                consecutiveContraryKeyPressCount = 0;
		            }
		        } else {
		            consecutiveContraryKeyPressCount = 0;
		            return;
		        }
		    } else {
		        translationAmount = 0.01f;  
		        rotationAmount = (float) Math.toRadians(1.0);  
		    }

		    switch (keycode) {
		        case KeyEvent.VK_LEFT:
		            doRotation(rotationAmount);
		            break;
		        case KeyEvent.VK_RIGHT:
		            doRotation(-rotationAmount);
		            break;
		        case KeyEvent.VK_UP:
		            doTranslation(new Vector3f(0, 0, translationAmount));
		            break;
		        case KeyEvent.VK_DOWN:
		            doTranslation(new Vector3f(0, 0, -translationAmount)); 
		            break;
		    }

		    lastKey = keycode;
		}

		private boolean isOppositeKey(int currentKey, int lastKey) {
		    switch (lastKey) {
		        case KeyEvent.VK_LEFT:
		            return currentKey == KeyEvent.VK_RIGHT;
		        case KeyEvent.VK_RIGHT:
		            return currentKey == KeyEvent.VK_LEFT;
		        case KeyEvent.VK_UP:
		            return currentKey == KeyEvent.VK_DOWN;
		        case KeyEvent.VK_DOWN:
		            return currentKey == KeyEvent.VK_UP;
		        default:
		            return false;
		    }
		}
 private void doRotation(double t) {
	 Transform3D oldTr=new Transform3D();
	 moveTg.getTransform(oldTr);
	 
	 Transform3D newTr=new Transform3D();
	 newTr.rotY(t);
	 
	 oldTr.mul(newTr);
	 moveTg.setTransform(oldTr);
 }

 
 private void doTranslation(Vector3f v) {
	 Transform3D oldTr=new Transform3D();
	 moveTg.getTransform(oldTr);
	 
	 Transform3D newTr=new Transform3D();
	 newTr.setTranslation(v);
	 
	 oldTr.mul(newTr);
	 moveTg.setTransform(oldTr);
	 
	 if(node!=null) {
		 Transform3D tr= new Transform3D();
		 Vector3f position=new Vector3f();
		 node.getLocalToVworld(tr);
		 tr.get(position);
		 System.out.println(position);
	 }
 }
 
}
