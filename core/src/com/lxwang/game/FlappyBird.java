package com.lxwang.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] bird;
	Texture topTube;
	Texture bottomTube;
	Texture gameover;
	Circle b_circle;
//	ShapeRenderer shapeRenderer;
	Rectangle[] topRec;
	Rectangle[] bottomRec;

	long timeStemp = 0;
	int flapState = 0;

	float birdY = 0;
	float velocity = 0;
	float gravity = 2;
	float gap = 400;
	float maxTubeOffset;
	Random generator;
	float tubeVelocity = 4;
	int n_tubes = 4;
	float[] tubeX = new float[n_tubes];
	float[] tubeOffset = new float[n_tubes];
	float distanceBetweenTubes;

	int gameState = 0; //
	int score = 0;
	int scoringTube = 0;
	BitmapFont font;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");

		bird = new Texture[2];
		bird[0] = new Texture("bird.png");
		bird[1] = new Texture("bird2.png");
		birdY = Gdx.graphics.getHeight()/2 - bird[0].getHeight()/2;

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		generator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth()/1.33f;
//		shapeRenderer = new ShapeRenderer();
		b_circle = new Circle();
		topRec = new Rectangle[n_tubes];
		bottomRec = new Rectangle[n_tubes];
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		gameover = new Texture("gameover.png");
		startGame();
	}

	public void startGame(){
		birdY = Gdx.graphics.getHeight()/2 - bird[0].getHeight()/2;
		for(int i = 0; i < n_tubes; i++){
			tubeOffset[i] = (generator.nextFloat() - 0.5f) * 2*maxTubeOffset;
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i*distanceBetweenTubes;
			topRec[i] = new Rectangle();
			bottomRec[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		timeStemp++;
		//draw background
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1) {
			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2){
				score++;
//				Gdx.app.log("Score", String.valueOf(score));
				if(scoringTube < n_tubes-1) scoringTube++;
				else scoringTube = 0;
			}
			if (Gdx.input.justTouched()){
				velocity = -30;
			}
			for(int i = 0; i < n_tubes; i++){
				if(tubeX[i] < - topTube.getWidth()) {
					tubeX[i] += n_tubes * distanceBetweenTubes;
					tubeOffset[i] = (generator.nextFloat() - 0.5f) * 2*maxTubeOffset;
				}else
					tubeX[i] -= tubeVelocity;

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topRec[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomRec[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}

			if(birdY > 0){
				velocity += gravity;
				birdY -= velocity;
			}else
				gameState = 2;
		}else if(gameState == 0){
			if(Gdx.input.justTouched())
				gameState = 1;
		}else if(gameState == 2){
			batch.draw(gameover, Gdx.graphics.getWidth()/2 - gameover.getWidth()/2, Gdx.graphics.getHeight()/2 - gameover.getHeight()/2);
			if(Gdx.input.justTouched()) {
				gameState = 1;
				startGame();
				scoringTube = 0;
				score = 0;
				velocity = 0;
			}
		}

		flapState = timeStemp % 8 < 4 ? 0 : 1;
		//draw bird
		batch.draw(bird[flapState], Gdx.graphics.getWidth()/2 - bird[flapState].getWidth()/2,  birdY);
		font.draw(batch, String.valueOf(score), 100, 200);

//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);
		b_circle.set(Gdx.graphics.getWidth()/2, birdY + bird[flapState].getHeight()/2, bird[flapState].getWidth()/2);
//		shapeRenderer.circle(b_circle.x, b_circle.y, b_circle.radius);

		for(int i = 0; i < n_tubes; i++){
//			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
//			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			if(Intersector.overlaps(b_circle, topRec[i]) || Intersector.overlaps(b_circle, bottomRec[i])){
				gameState = 2;
			}
		}
//		shapeRenderer.end();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
