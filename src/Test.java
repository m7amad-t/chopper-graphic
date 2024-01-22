
import javax.media.opengl.*;
import javax.media.opengl.awt.*;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.sound.sampled.*;
import javax.media.opengl.glu.*;

import java.awt.event.*;
import java.util.Random;
import java.io.File;
import java.io.IOException;

import javax.swing.*;


public class  Test extends JFrame implements GLEventListener , KeyListener {
	
	
	Random random = new Random();
	
	GLCanvas canvas;
	
	Animator an;
	
	GL2 gl;

//	use to display texts
	private GLUT glut = new GLUT();
	
	
	
	int [] treeSizes = new int [24]; 
	
	
//	this is Chopper engine sound
	
//	sound of flying
	Clip startUp; 
	Clip flying; 
	
	
//	this is for traffic light timer & controller
	double traffic = 0.0 ; 
	boolean isT_r = true;
	boolean isT_o = false;
	boolean isT_g = false; 
	
//	this is use to flirting timer & controller
	double flirting = 0.0 ; 
	boolean isRed = false; 
	
//	is the engine on ???
	boolean isOn = false; 
	
//	its can fly ? 
	boolean isFly = false; 
	
//	this is for top (header) fan of the chooper
	double from = 30; 
	double to = 210;
	
	
//	this is tell what is the chooper state >>>> what direction its moving? 
	boolean toUp = false;	
	boolean toDown = false;
	boolean toLeft = false; 
	boolean toRight = false; 
	
//	this is for fire on engine
	double fireWork = 230; 
	double splashWork = 230; 
	boolean isInc = true; 
	
	
//	this is for day and night mode
	boolean isDay = true; 
	
	
//	this is for rotating the chooper
	double rotate = 0 ; 
	
//	this is for tail fan
	double fromTail = -120; 
	double toTail = -170; 
	
//	this is for shifting chooper on vertical line
	double translateY = 0 ; 
	
//	this is for shifting chooper on Horizontal line
	double translateX = 0 ; 
	
//	this is for fast/slow mode
	boolean isFast = false; 
	
//	this is for stars location 
	int [][] starXY = new int [200][3]; 
	
	
//	
	
	
	public Test()
	{
	   super("LAB2_ABSTRACT");
	   canvas=new GLCanvas();
	   add(canvas);
	   canvas.addGLEventListener(this);
	   canvas.addKeyListener(this);
	   an=new Animator(canvas);
	   canvas.requestFocus();
	   an.start();
	   setSize(1800,1000);
	   setVisible(true);
	   setLocation(20,20);
	   
	   setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public void init(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		gl=arg0.getGL().getGL2();
		GLU glu=new GLU();
		if(isDay){
			gl.glClearColor(1f,1f,1f,1f);
		}else {
			gl.glClearColor(0f,0f,0f,1f);
		}
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		glu.gluOrtho2D(-900, 900, -500, 500);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gen_stars(); 
		generateTrees();
		
		

	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	public void display(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
//		init
	
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		
		
	
		traffic_logic_handler();
		
		
		
//		this is use to toggle the red/white led on the chopper
		if(isOn){
			if(flirting >20){
				flirting = 0.0 ; 
				isRed = !isRed ; 
			}
//			incrementing flirting value 
			flirting += 0.1; 
		}
		
		
//		////////////////////////////////    Sky        \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
//		this is draw sky (blue polygon)
		drawSky(); 
		
		
		
//		///////////////////////////////     stars (on night) \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
//		draw stars
//		check if its day or night 
		if(!isDay){
			gl.glColor3f(200/255f , 240/200f , 30/255f); 
			for (int i =0; i<200 ; i++){
				
				drawCircle(starXY[i][0], starXY[i][1],starXY[i][2] );
			}
		}
		
		
		

//		this is for mountains
		
//					  x     y   height   width    r   g     b
//       ////////////////////////////////// Mountains  \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
		drawMountains();
		

		
		
		
//       ////////////////////////////////// Ground(grass) \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
//		
		drawGround();
		
		
//		///////////////////////////////// 		city		\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
		drawCity(); 
		
		
	
		
//		////////////////////////      this is chopper part    \\\\\\\\\\\\\\\\\\
		
//		/////////////////////////     chopper Logic           \\\\\\\\\\\\\\\\\\\\\\\\ 

		chopper_position_handler();
		
//		/////////////////////////  chopper positioning \\\\\\\\\\\\\\\\\\\\\\
		gl.glPushMatrix();
		
//		chopper translate & rotate functionality
		
		gl.glTranslated(translateX, translateY, 0);
		gl.glRotated(rotate,  0 , 0, 1);	
		
// 		////////////////////////   chopper \\\\\\\\\\\\\\\\\\\\\\\\\\\ 
		draw_chooper(); 
		
		
//		Popping (chopper Translate & Rotate) matrix
		gl.glPopMatrix();
		
		
//		////////////////////// day/night logic \\\\\\\\\\\\\\\\\\\\\\\
		
		drawTrees();
		if(!isDay){
			makeItNight(); 
		}
		
		drawText("! Press H to show keys", -880, -440, 40);
		
	
		
		
		
	}
	
	private void drawTrees(){
//		tree near of the mountains
		drawTree(-800, -200, 120);
		drawTree(-840, -200, 140);
		drawTree(-750, -200, 150);
		drawTree(-700, -200, 120);
		drawTree(-880, -200, 140);
		drawTree(-920, -200, 110);
		
		
//		tree by the highway road
		
		for (int i = 0 ; i <24 ; i++){
			int xBase = -890; 
			drawTree(((xBase)+(60*(i+1))), -350, treeSizes[i]);
		}
	}

	private void generateTrees (){
		for (int i = 0 ; i <24 ; i++){
			int height = (int) random.nextInt(40) + 100;	
			treeSizes[i] = height; 
		}
	}


	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] arg)
	{
		new Test();
	}

	
	
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
		
		
		if(arg0.getKeyChar() == 'h' || arg0.getKeyChar() == 'H'){
			showMessageDialog(); 
		}
		
//		engine on/off
		if(arg0.getKeyChar() == ' '){
			
//			this is restrict the chopper engine on/off only when its on ground
			if(translateY <= 0 ){
				if(isOn){
//					turn off the audio
					isFly = false;
					isOn = false; 
					
					startUp.stop(); 
					startUp.close();
					
					
					
					flying.stop();
					flying.close();
					
					
					 
				}else {
					isOn = true; 
					startUpSound(); 
					
				}
			}	
		}
		
		
//		to change day/night mode
		if(arg0.getKeyChar() == 'm' || arg0.getKeyChar() == 'M'){
			isDay = !isDay ;  	
		}

//		to change fast/slow mode 
		if(arg0.getKeyCode() == 16){
			isFast = !isFast; 
		}
		
//		check if engine on then ::
		
		if(isOn){
			if(isFly){
				
//				move the chopper to UP
				if(arg0.getKeyCode() == 38){
					toUp = true;
					if(rotate < 0 && translateY < 28){
						rotate = 0 ; 
					}else if (rotate >0 && translateY <4){
						rotate = 0 ; 
					}
					if(rotate <0 ){
						rotate ++; 
					}else if (rotate > 0 ){
						rotate--; 
					}
				}
				
//				move the chopper to Down
				if(arg0.getKeyCode() == 40){
					toDown = true; 
					if(rotate < 0 && translateY < 28){
						rotate = 0 ; 
					}else if (rotate >0 && translateY <4){
						rotate = 0 ; 
					}
					if(rotate <0 ){
						rotate ++; 
					}else if (rotate > 0 ){
						rotate--; 
					}
				}
				
//				move the chopper to left 
				if(arg0.getKeyCode() == 37){
					toLeft = true;  
				}
				
//				move the chopper to right 
				if(arg0.getKeyCode() == 39){
					toRight = true; 
				}
				
//				rotate the chopper to Down
				if(arg0.getKeyChar() == 'q' || arg0.getKeyChar() == 'Q'){
					
//					check the chopper if its near of the ground
					if(translateY >=10 && rotate <=10){
						rotate +=1; 	
					} 
				}
				
//				rotate the chopper to Up
				if(arg0.getKeyChar() == 'e' || arg0.getKeyChar() == 'E'){
//					check the chopper if its near of the ground
					if(translateY >=40 && rotate >=-10){
						rotate -=1; 	
					}
					
				}
//				
//				rotate chopper to center 
				if(arg0.getKeyChar() == 'w' || arg0.getKeyChar() == 'W'){
						if( rotate >0 ){
							rotate--; 
						}else if (rotate <0){
							rotate++; 
						}
				}
			
		}
		
			
			
			
			
		}
		
		
		
	}
	
//	traffic logic
	private void traffic_logic_handler(){
//		traffic logic

		
	//		timer 
	traffic += 0.04; 
		
		if(traffic <40){
			isT_r = true; 
			isT_g = isT_o = false; 
		}else if(traffic <80){
			isT_g = isT_r = false; 
			isT_o = true; 
		}else if (traffic < 120){
			isT_g = true ;
			isT_r = isT_o = false; 
		}else if(traffic < 160){
			isT_g = isT_r = false; 
			isT_o = true; 
		}else {
			traffic = 0 ; 
		}
	}
	
	
//	traffic drawer
	public void drawTraffic(){
		
		
		
//		traffic tower
		gl.glColor3f(0.35f,0.3f,0.3f); 
		gl.glLineWidth(6);
		gl.glBegin(GL2.GL_LINES); 
		
		gl.glVertex2i(770, -230);
		gl.glVertex2i(760, -345);
		gl.glEnd(); 
		
		
//		traffic box
		gl.glColor3f(0.2f,0.2f,0.2f); 
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2i(780, -230);
		gl.glVertex2i(760, -230);
		gl.glVertex2i(756, -270);
		gl.glVertex2i(776, -270);
		gl.glEnd(); 
		
		
//		red light 
		if(isT_r){
			gl.glColor3f(1f,0f,0f); 	
		}else {
			gl.glColor3f(0.4f,0.4f,0.4f); 
		}
		drawCircle(770, -237, 5);
		
		
//		orange light 
		if(isT_o){
			gl.glColor3f(255/255f,120/255f,0/255f); 	
		}else {
			gl.glColor3f(0.4f,0.4f,0.4f); 
		}
		drawCircle(769, -250, 5);
		
//		green light
		if(isT_g){
			gl.glColor3f(0f,1f,0f); 	
		}else {
			gl.glColor3f(0.4f,0.4f,0.4f); 
		}
		drawCircle(768, -262, 5);
	}
	
	
//	drawing city 
	public void drawCity(){
		drawTower(1100 ,-285 ,0.7);
		drawTower(550 ,-200 ,1);
		drawRoad(); 
		drawTraffic();
		
	}
	
//  draw road 
	public void drawRoad (){
		
//		draw road between city and highway 
		
		gl.glColor3f(0f,0f,0f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2i(600, -200);
		gl.glVertex2i(920, -450);
		gl.glVertex2i(1070, -450);
		gl.glVertex2i(750, -200);
		
		gl.glEnd();
		
//	  draw highway road
	    gl.glColor3f(0f,0f,0f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2i(920, -350);
		gl.glVertex2i(920, -450);
		gl.glVertex2i(-920, -450);
		gl.glVertex2i(-920, -350);
		gl.glEnd();
		
//		stipple line for the city-> highway
	    draw_stipple_line(675, 920, -200, -400);
	    
//	    stipple line for the highway
		draw_stipple_line(-920, 900, -400, -400);
		
	}
	
	
//	draw Stipple line for the roads 
	public void draw_stipple_line(int x1 , int x2 , int y1 , int y2){
		gl.glColor3f(1,1,0);
		gl.glEnable(GL2.GL_LINE_STIPPLE);
	    gl.glLineStipple(1,(short)0xFF00 );
	    gl.glLineWidth(2);
	    gl.glBegin(GL2.GL_LINES);
	    gl.glVertex2i(x1,y1);
	    gl.glVertex2i(x2, y2);
	    gl.glEnd();
	    gl.glDisable(GL2.GL_LINE_STIPPLE);
	}
	
	
//	draw tower 
	public void drawTower(int x , int y , double scale){
		gl.glPushMatrix();
		gl.glScaled(scale, scale, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(50/255f, 43/255f, 50/255f);
		gl.glVertex2i(x, y);
		gl.glVertex2i(x+260, y);
		gl.glVertex2i(x+260, y+650);
		gl.glVertex2i(x, y+650);
		gl.glEnd(); 
		
		for(int i = 1 ; i <=14 ; i++){
			
//			to pass 5 and 9 floor 
			if(i == 5 || i == 9 ){
				i++;  
			}
			for (int j = 1 ; j<=7 ; j++){
//				to pass that window that on column 4 
				if(j == 4){
					j++;  
				}
				gl.glBegin(GL2.GL_POLYGON);
				gl.glColor3f(200/255f, 200/255f, 20/255f);
				int startingX = x+((20 +10)*j); 
				int startingY = y+((30+10)*i); 
				gl.glVertex2i(startingX , startingY);
				gl.glVertex2i(startingX+20, startingY);
				gl.glVertex2i(startingX+20, startingY+30);
				gl.glVertex2i(startingX, startingY+30);
				gl.glEnd(); 
			}
		}
		gl.glPopMatrix();
	}

//	drawing sky
	public void drawSky(){


		
		
		gl.glColor3f(27/255f ,  120/255f, 230/255f);
		
		
		
		
		gl.glBegin(GL2.GL_POLYGON); 
		gl.glVertex2i(900 , 500);
		gl.glVertex2i(-900,500);
		gl.glVertex2i(-900,-500);
		gl.glVertex2i(900,-500);
		
		
			gl.glColor3f(27/255f ,  110/255f, 210/255f);
		
		
		gl.glBegin(GL2.GL_POLYGON); 
		gl.glVertex2i(900 , 500);
		gl.glVertex2i(-900,500);
		gl.glVertex2i(-900,-500);
		gl.glVertex2i(900,-500);
		
		
			gl.glColor3f(30/255f ,  150/255f, 240/255f);
	
		
		gl.glBegin(GL2.GL_POLYGON); 
		gl.glVertex2i(900 , 500);
		gl.glVertex2i(-900,500);
		gl.glVertex2i(-900,-500);
		gl.glVertex2i(900,-500);
		
		gl.glEnd();
		
//		end of the sky
	}
	
//	draw ground 
	public void drawGround(){
//		start of the ground
		
		gl.glColor3f(40/255f ,  190/255f, 30/255f);
		
		
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2i(900 , -200);
		gl.glVertex2i(-900,-200);
		gl.glVertex2i(-900,-500);
		gl.glVertex2i(900,-500);
		
		
		gl.glColor3f(25/255f ,  175/255f, 35/255f);
		
		
		
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2i(900 , -200);
		gl.glVertex2i(-900,-200);
		gl.glVertex2i(-900,-500);
		gl.glVertex2i(900,-500);
		
		gl.glEnd();

//		end of the ground
	}
	
//	chopper logic handler 
	public void chopper_position_handler(){
		if(toUp){
			if(translateY < 550){
			
				if(isFast){
					translateY +=4; 	
				}
				translateY +=2 ;
			}
			toUp = false;
		}
		if(toDown){
//			check if chopper >>> rotating ? => rotate = 0 
			if(translateY <= 5){
				rotate = 0 ; 
			}
			
//			restrict the chopper from going to down into grass
			if(translateY >0 ){
				
				if(isFast){
					translateY -=4; 	
				}
				translateY -=2 ; 
			}
			toDown = false; 
		}
		
		if(toLeft){
			
			if(isFast){
				translateX -=8 ; 
			}else {
				translateX -=2; 	
			}
			toLeft = false; 
//			check if chopper is beyond the area >>> move to right of the screen
			if(translateX <= -1250.0){
				translateX = 920; 
			}
		}
		
		if(toRight){
		
			if(isFast){
				translateX +=8; 
			}else {
				translateX +=2; 	
			}
			
			toRight = false; 
			
//			check if chopper is beyond the area >>> move to left of the screen
			if(translateX >= 920.0){
				translateX = -1250.0; 
			}
		}
	}
	
//	this is to draw all mountains
	public void drawMountains(){
//					   x    y   height   width   R     G     B
		drawMountain(-700, -200,  600 ,   600  , 110  ,80 , 70);
		drawMountain(-700, 270,  130 ,   130  , 255  ,255 , 255);
		
		drawMountain(-630, -200, 400  , 650  ,  80  ,80 , 59);
		drawMountain(-850, -200, 500, 400 , 80  ,50 , 40);
		drawMountain(-850, 200, 100, 81 , 255  ,255,255);

		drawMountain(-750, -200, 200, 500 , 40  ,34 , 30);

	}
	
//	this is for startup the chopper engine sound
	public void startUpSound(){
		try { 

			AudioInputStream startUp_audio = AudioSystem.getAudioInputStream
					(new File("sound//startUp.wav"));
		
			
		
		startUp = AudioSystem.getClip();
		startUp.open(startUp_audio);
		startUp.setFramePosition(50000);
		startUp.start();
		startUp.addLineListener( new LineListener() {
				
				@Override
				public void update(LineEvent event){
					
					if(!isOn){
						startUp.stop();
						startUp.close();
					}else {
						if(event.getFramePosition() == startUp.getFrameLength()){
							if(isOn){
								flyingSound();
								isFly = true; 
							}
					
							
						}
					}
					
					
				}
			} 
		);
			
		}
		catch(UnsupportedAudioFileException e1) 
		{
			System.out.println(e1.getStackTrace());
			System.out.println("first catch");
		
		}catch(IOException e2)
		{System.out.println(e2.getStackTrace());
		
		System.out.println("second catch");}
		
		catch(LineUnavailableException e3) {
			System.out.println("therd catch");
			System.out.println(e3.getStackTrace());
			
		}
	}
	
	
	
//	this is for flying chopper sound 
	public void flyingSound(){
		try { 
			AudioInputStream audio = AudioSystem.getAudioInputStream
					(new File("sound//on.wav"));
		
		flying = AudioSystem.getClip();
		flying.open(audio);
		flying.start();
		flying.addLineListener( new LineListener() {
				
				@Override
				public void update(LineEvent event){
					
					if(!isOn || !isFly){
						flying.stop();
						flying.close();
						
					}else {
						if(event.getType() == LineEvent.Type.STOP){
							flying.setFramePosition(0);
							flying.start(); 
						}
					}
					
					
				}
			} 
		);
			
		}
		catch(UnsupportedAudioFileException e1) 
		{
			System.out.println(e1.getStackTrace());
			System.out.println("first catch");
		
		}catch(IOException e2)
		{System.out.println(e2.getStackTrace());
		
		System.out.println("second catch");}
		
		catch(LineUnavailableException e3) {
			System.out.println("therd catch");
			System.out.println(e3.getStackTrace());
			
		}
	}

	
//	key release function
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
//	key typed function 
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	
	}
	
	
//  Chopper drawer
	private void draw_chooper(){

//		base of the chopper
		gl.glLineWidth(6);
		gl.glColor3f(0f, 0f, 0f);
		
		

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2i(50, -200);
		gl.glVertex2i(180, -200);
		gl.glEnd(); 
		
		gl.glLineWidth(5);
//	end of the base of the chopper
		
		
//		bridge between chopper and base
//		left one
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2i(60, -200);
		gl.glVertex2i(70, -165);
		gl.glEnd(); 
		
//		right one
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2i(170, -200);
		gl.glVertex2i(160, -145);
		gl.glEnd(); 
		
//		end of the base components and the bridge 
		
		
		
//		body of the chopper

//		
		gl.glColor3f(50/255f, 0.0f, 0.0f);
//		bottom right 1/4 circle
		drawCircleQuadrant( 180,-130, 48, -90, 0);
		
		
//		top right 1/4 circle
//		drawCircleQuadrant( 180,-130, 48, 0, 90);
		
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2i(180, -82);
		gl.glVertex2i(180, -138);
		gl.glVertex2i(220, -138);
		gl.glVertex2i(210, -118);
		gl.glVertex2i(205, -108);
		gl.glVertex2i(200, -98);
		gl.glVertex2i(190, -88);
		gl.glVertex2i(180, -82);
		

		gl.glEnd(); 
		
		
//		engine part
		
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(60/255f, 0.0f, 0.0f);
		gl.glVertex2i(225, -95);
		gl.glVertex2i(198, -97);
		gl.glVertex2i(210, -125);
		gl.glVertex2i(230, -120);
	
		gl.glEnd(); 
		
//		end of the engine 
		
//		fire on the engine
		if(isOn){
			
//			fire logic
			if(isFly){
				double incValue = 0.04; 
				double decValue = 0.1; 
				
				
				if(fireWork > 245){
					isInc = false; 
				}else if(fireWork <229){
					isInc = true;
				}
				if(isInc){
					fireWork +=incValue; 
				}else {
					fireWork -=decValue; 
				}
//				main fire
				gl.glEnable(GL2.GL_BLEND);
				gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
				gl.glBegin(GL2.GL_POLYGON);
				gl.glColor4f(200/255f, 0.1f, 0.1f , 0.9f);
				
				gl.glVertex2i(225, -95);
				gl.glVertex2i(230, -120);
				gl.glVertex2i((int)fireWork, -102);
			
				gl.glEnd(); 
				
			}
			
//			splash logic
			
			int minX = 250 ; 
			int maxX = 300;
			if(!isFly){
				maxX -=40; 
			}
			
			splashWork = random.nextInt(maxX - minX +1)+minX; 
			
//			splash fire (spark || fire TAILS)
			
			gl.glBegin(GL2.GL_POLYGON);
			gl.glColor4f(200/255f, 0.1f, 0.1f , 0.4f);
			
			gl.glVertex2i(225, -95);
			gl.glVertex2i(230, -120);
			gl.glVertex2i((int)splashWork, -96);
			
			gl.glEnd(); 
			
			
		}
		
//		

		
		
		gl.glColor3f(50/255f, 0.0f, 0.0f);
//		
//		polygon between right and left 1/4 circles 
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2i(80, -82);
		gl.glVertex2i(80, -178);
		gl.glVertex2i(180, -178);
		gl.glVertex2i(180, -82);

		gl.glEnd(); 
		

		
//		door of the chopper
		
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(60/255f, 0.10f, 0.10f);
		gl.glVertex2i(90, -90);
		gl.glVertex2i(90, -170);
		gl.glVertex2i(150, -170);
		gl.glVertex2i(150, -90);
		gl.glEnd(); 
//		end of the door
		
		
//		bottom left 1/4 circle 
		gl.glColor3f(50/255f, 0.0f, 0.0f);
		drawCircleQuadrant( 80,-130, 48, 180, 270);
		
//		small half circle left front
//		bottom part
		drawCircleQuadrant( 35,-153, 25, 180, 270);
//		top part
		drawCircleQuadrant( 35,-153, 25, 180, 90);
		
//		rest between bottom
		gl.glBegin(GL2.GL_POLYGON); 
		gl.glVertex2i(20 , -140);
		gl.glVertex2i(30,-178);
		gl.glVertex2i(90,-178);
		gl.glVertex2i(90,-160);
		
		gl.glEnd(); 
		
//		person in the chopper
//		head of the person
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		drawCircle(68, -117, 10);
//		neck of the person
		gl.glLineWidth(3);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2i(70, -116);
		gl.glVertex2i(70, -130);
		gl.glEnd(); 
		


		
		
//		glass part 1/4 of the circle 
		   gl.glEnable(GL2.GL_BLEND);
		    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		    gl.glColor4f(0.5f ,0.5f ,0.5f ,0.5f); 
	    drawCircleQuadrant( 80,-130, 48, 180, 90);
		
	    
	    
//		tail of the chooper
	    gl.glColor3f(50/255f, 0.0f, 0.0f);
	   
		gl.glBegin(GL2.GL_POLYGON); 
		gl.glVertex2i(200 , -175);
		gl.glVertex2i(330,-150);
		gl.glVertex2i(330,-140);
		gl.glVertex2i(200,-125);
		
		gl.glEnd(); 
		
//		end of choper's tail
//		
//		tail light 
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		if(!isOn){
			gl.glColor4f(1f,0.5f,0.5f , 0.4f); 
		}else {
//			
			if(isRed){
				gl.glColor4f(1f,0.0f,0.0f , 0.7f);
			}else {
				gl.glColor4f(1f,1f,1f , 0.7f);
			}
		}
		 
		gl.glBegin(GL2.GL_POLYGON); 
		
		gl.glVertex2i(332,-140);
		gl.glVertex2i(332,-150);
		
		
		gl.glVertex2i(335,-150);
		gl.glVertex2i(337,-146);
		
		
		
		
		
		gl.glVertex2i(337,-144);
		gl.glVertex2i(335,-140);
		
		
		
		gl.glEnd(); 
		
		
//		top bridge between chopper and top fan
		gl.glColor3f(0f ,  0f, 0f);
		
		gl.glBegin(GL2.GL_LINES); 
		gl.glVertex2i(120 , -82);
		gl.glVertex2i(120,-65);
		gl.glEnd(); 
		
//		the top 1st hill for fan
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2i(125,-73);
		gl.glVertex2i(115,-73);
		gl.glVertex2i(110,-75);
		gl.glVertex2i(105,-82);
		gl.glVertex2i(135,-82);
		gl.glVertex2i(130,-75);
//		gl.glVertex2i(130,-73);
		
		gl.glEnd(); 
		
		
		 
		
		
//		header light
		
		if(!isOn){
			gl.glColor4f(0.5f ,  0.5f, 0.5f  , 0.4f);
			
		}else {
			if(!isRed){
				gl.glColor4f(1f,0.0f,0.0f , 0.7f);
			}else {
				gl.glColor4f(1f,1f,1f , 0.7f);
			}
		}
		
		gl.glBegin(GL2.GL_POLYGON); 
	
		gl.glVertex2i(115,-64);
		gl.glVertex2i(125,-64);
		
		gl.glVertex2i(125,-60);
		
		gl.glVertex2i(123,-55);
		
		gl.glVertex2i(118,-55);
		
		gl.glVertex2i(115,-60);
		
		gl.glEnd();
//		end of the header light 
		
		
		
		
//		header fan logic
		if(isOn){
			
			if(from >=210){
				double temp = from ; 
				from = to ; 
				to =  temp; 
			}else {
				if(isFly){
					from = from +8; 
					to = to -8; 
				}else {
					from = from +4; 
					to = to -4; 
				}
				
			}
		}
		
		
		
		
//		header fan 
		gl.glLineWidth(4);
		gl.glColor3f(0f ,  0f, 0f);
		gl.glBegin(GL2.GL_LINES); 
		gl.glVertex2i((int)from , -65);
		gl.glVertex2i((int)to,-65);
		gl.glEnd();
//		end of the header fan 
		


//		tail fan logic
		double tailEnd = -170; 
		
//		check if engine is on or not
		if(isOn){
			if( fromTail == tailEnd ){
				fromTail = -120; 
				toTail = -170; 
			}else {
				fromTail = fromTail -1; 
				toTail = toTail +1; 
			}
		}
		
		gl.glColor3f(0f ,  0f, 0f);
//		tail fan
		gl.glLineWidth(4);
		gl.glColor3f(0f ,  0f, 0f);
		gl.glBegin(GL2.GL_LINES); 
		gl.glVertex2i(330,(int) fromTail);
		gl.glVertex2i(330,(int) toTail);
		gl.glEnd();
//		end of the tail fan
		

	}
	
//	draw 1/4 circle
	
	
//	this is use to draw 1/4 of a circle 
	private void drawCircleQuadrant( float centerX, float centerY, float radius, float startAngle, float endAngle) {
    int segments = 100;
    float angleStep = (endAngle - startAngle) / segments;

    gl.glBegin(GL.GL_TRIANGLE_FAN);
 
//    gl.glColor3f(0.7f, 0.7f, 0.7f); // Set color to gray

    gl.glVertex2f(centerX, centerY); // Center of the circle

    for (int i = 0; i <= segments; i++) {
        float angle = (float) Math.toRadians(startAngle + i * angleStep);
        float x = centerX + (float) Math.cos(angle) * radius;
        float y = centerY + (float) Math.sin(angle) * radius;
        gl.glVertex2f(x, y);
    }

    gl.glEnd();
}

	
//	draw complete circle
	private void drawCircle(int x,int y,int radius){///creating circle 
		
	  gl.glBegin(GL2.GL_TRIANGLE_FAN);
      gl.glVertex2f(x, y); // Center of the circle
      int numPoints = 200; 
      
      for (int i = 0; i <= numPoints; i++) {
          double angle = 2.0 * Math.PI * i / numPoints;
          float newX = x + (float) (radius * Math.cos(angle));
          float newY = y + (float) (radius * Math.sin(angle));
          gl.glVertex2f(newX, newY);
      }

      gl.glEnd();
	 
      
      
	}

	
//	make the game mode to night 
	private void makeItNight(){
		gl.glEnable(GL2.GL_BLEND); 
		
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glBegin(GL2.GL_POLYGON); 
		gl.glVertex2i(900 , 500);
		gl.glVertex2i(-900,500);
		gl.glVertex2i(-900,-500);
		gl.glVertex2i(900,-500);
		gl.glEnd(); 
	}
	
	

//	generate star X , Y and Radius
//	this is generate stars (position) and (radius)
	private void gen_stars (){
		
		
		int minX = -900 ; 
		int maxX = 900;
		int minY = -200; 
		int maxY = 500; 
		
		int minR = 1; 
		int maxR = 2; 
		
		for (int i = 0 ; i <200 ; i++){
			int x = random.nextInt(maxX - minX +1)+minX; 
			int y = random.nextInt(maxY - minY +1)+minY; 
			int r = random.nextInt(maxR - minR +1)+minR; 
			starXY[i][0] = x; 
			starXY[i][1] = y; 
			starXY[i][2] = r; 
		
		}
	}


//  to draw mountains
	
//	this is draw a mountain ( used in drawMountains Method )
	private void drawMountain( int x, int y, int hight , int width , int r , int g  , int b ) {
	    gl.glColor3f(r/255f, g/255f, b/255f); 

	    gl.glBegin(GL2.GL_POLYGON);
	    gl.glVertex2f(x-(width/2), y);
	    gl.glVertex2f(x +(width/2), y);
	    gl.glVertex2f(x , y + hight);
	    gl.glEnd();
	}

//	this is help dialog
	private void showMessageDialog(){
		 JOptionPane.showMessageDialog(
	                null, // Parent component (null for default)
	                "Engine start/stop press space\nFor moving chopper use arrow keys\nPress Q to rotate downward\nPress E to rotate Upward \nPress W to restore balance\nPress Shift key to change slow/fast mode \nPress M to change Day/Night Mode.", // Message text
	                "Information", // Dialog title
	                JOptionPane.INFORMATION_MESSAGE // Message type
	        );
	}
	
//	this is use to display text
	private void drawText( String text, float x, float y, int fontSize) {
        gl.glColor3f(1.0f, 1.0f, 1.0f); // White color for text

        gl.glRasterPos2f(x, y);

        // Loop through each character in the string and draw it
        for (char character : text.toCharArray()) {
        	
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_12, character);
        }
    }

//	this is use to draw complete tree using rectangle + triangle
	private void drawTree( int x, int y, int height) {
	        // Draw tree
		    // Brown color for trunk
	        gl.glColor3f(0.5f, 0.3f, 0.1f); 
	        drawRectangle(gl, x - 10, y, 20, height / 3);

	        // Draw canopy (triangle)
	     // Green color for head
	        gl.glColor3f(0.0f, 0.8f, 0.0f); 
	        drawTriangle(gl, x, y + height / 3, 2 * height / 3);
	    }

//	this is draw rectangle of the trees (the trunk)
	private void drawRectangle(GL2 gl, int x, int y, int width, int height) {
	        gl.glBegin(GL2.GL_POLYGON);
	        gl.glVertex2f(x, y);
	        gl.glVertex2f(x + width, y);
	        gl.glVertex2f(x + width, y + height);
	        gl.glVertex2f(x, y + height);
	        gl.glEnd();
	    }

//	this is draw a head of the tree (bush)
	private void drawTriangle(GL2 gl, int x, int y, int size) {
	        gl.glBegin(GL2.GL_POLYGON);
	        gl.glVertex2i(x - size / 2, y );
	        gl.glVertex2i(x + size / 2, y );
	        gl.glVertex2i(x, y+size);
	        gl.glEnd();
	    }


	


}



