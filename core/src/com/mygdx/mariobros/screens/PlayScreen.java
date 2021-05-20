package com.mygdx.mariobros.screens;

import Tools.B2WorldCreator;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mariobros.MarioBros;
import com.mygdx.mariobros.scenes.Hud;
import com.mygdx.mariobros.sprites.Mario;


import java.awt.*;

public class PlayScreen implements Screen {
    private MarioBros game;
    private TextureAtlas atlas;
    private Mario player;
    private Hud hud;
    private OrthographicCamera gameCam;
    private Viewport gamePort;

    //Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;

    public PlayScreen(MarioBros game){
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FillViewport(MarioBros.V_WIDTH/MarioBros.PPM,MarioBros.V_HEIGHT/MarioBros.PPM,gameCam);
        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map,1/MarioBros.PPM);
        gameCam.position.set(gamePort.getWorldWidth()/2,gamePort.getWorldHeight()/2,0);

        world = new World(new Vector2(0,-10),true);
        b2dr = new Box2DDebugRenderer();
        new B2WorldCreator(world,map);
        player = new Mario(world,this);



    }
    public void handleInput(float dt){
        if(Gdx.input.isKeyJustPressed(Input.Keys.W) ){
            player.b2body.applyLinearImpulse(new Vector2(0,4f),player.b2body.getWorldCenter(),true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) && player.b2body.getLinearVelocity().x <= 2){
            player.b2body.applyLinearImpulse(new Vector2(0.1f,0),player.b2body.getWorldCenter(),true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && player.b2body.getLinearVelocity().x >= -2){
            player.b2body.applyLinearImpulse(new Vector2(-0.1f,0),player.b2body.getWorldCenter(),true);
        }


    }

    public void update(float dt){
        handleInput(dt);

        world.step(1/60f,6,2);
        player.updata(dt);
        gameCam.position.x = player.b2body.getPosition().x;
        gameCam.update();
        renderer.setView(gameCam);
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        update(delta);
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();


        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();

    }
}
