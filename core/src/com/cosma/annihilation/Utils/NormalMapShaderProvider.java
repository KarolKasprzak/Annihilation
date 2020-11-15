package com.cosma.annihilation.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Box2dLight.Light;
import com.cosma.annihilation.Box2dLight.RayHandler;
import com.cosma.annihilation.Editor.CosmaMap.GameMap;

import java.util.Arrays;

public class NormalMapShaderProvider {

    private Vector3 lightPosition = new Vector3();
    private float[] lightPositionArray = new float[21];
    private float[] lightColorArray = new float[21];
    private float[] intensityArray = new float[7];
    private float[] distanceArray = new float[7];
    private Array<Light> activeLights = new Array<>();

    private OrthographicCamera camera;
    private RayHandler rayHandler;
    private GameMap gameMap;
    private ShaderProgram shader;
    private ShaderProgram flipShader;



    public NormalMapShaderProvider(OrthographicCamera camera, RayHandler rayHandler, GameMap gameMap) {
        this.gameMap = gameMap;
        this.camera = camera;
        this.rayHandler = rayHandler;
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal("shaders/normalMap/ver.glsl").readString(), Gdx.files.internal("shaders/normalMap/frag.glsl").readString());
        if (!shader.isCompiled())
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        shader.begin();
        shader.setUniformi("u_texture", 0);
        shader.setUniformi("u_normals", 1);
        shader.end();
        flipShader = createFlipShader();

    }

    public void updateMap(GameMap gameMap){
        this.gameMap = gameMap;
    }

    public ShaderProgram getRenderShader() {
        return shader;
    }

    public ShaderProgram getFlipShader() {return flipShader;}

    private ShaderProgram createFlipShader () {
        String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "uniform mat4 u_projTrans;\n" //
                + "varying vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "   v_color.a = v_color.a * (255.0/254.0);\n" //
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n";
        String fragmentShader = "#ifdef GL_ES\n" //
                + "#define LOWP lowp\n" //
                + "precision mediump float;\n" //
                + "#else\n" //
                + "#define LOWP \n" //
                + "#endif\n" //
                + "varying LOWP vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "uniform sampler2D u_texture;\n" //
                + "void main()\n"//
                + "{\n" //
                + "  vec4 color = v_color * texture2D(u_texture, v_texCoords);\n" //
                + "  color.r = 1.0 - color.r;\n" //
                + "  gl_FragColor = color;\n" //
                + "}";

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        return shader;
    }





    public void prepareData(boolean flipX){
        Arrays.fill(lightColorArray, 0);
        Arrays.fill(lightPositionArray, 0);
        Arrays.fill(intensityArray, 0);
        Arrays.fill(distanceArray, 0);
        activeLights.clear();
        for (Light light : rayHandler.getLightList()) {
            if(light.isRenderWithShader()){
                activeLights.add(light);
            }
//            if (camera.frustum.sphereInFrustum(light.getX(), light.getY(), 0, light.getDistance())) {
//                activeLights.add(light);
//            }
        }
        for (int i = 0; i < activeLights.size; i++) {
            if (i < 7) {
                Light light = activeLights.get(i);
                lightPosition.x = light.getX();
                lightPosition.y = light.getY();
                lightPosition.z = 0;

                camera.project(lightPosition);

                lightPositionArray[i * 3] = lightPosition.x;
                lightPositionArray[1 + (i * 3)] = lightPosition.y;
                lightPositionArray[2+(i*3)] = light.getLightZPosition();

                lightColorArray[i * 3] = light.getColor().r;
                lightColorArray[1 + (i * 3)] = light.getColor().g;
                lightColorArray[2 + (i * 3)] = light.getColor().b;

                intensityArray[i] = light.getIntensityForShader();
                distanceArray[i] = light.getLightDistanceForShader();
            }
        }
        if(activeLights.size < 7){
            shader.setUniformi("arraySize",activeLights.size);
        }else{
            shader.setUniformi("arraySize",7);
        }

        shader.setUniform1fv("intensityArray",intensityArray,0,7);
        shader.setUniform1fv("distanceArray",distanceArray,0,7);

        shader.setUniform3fv("lightPosition[0]", lightPositionArray, 0, 21);
        shader.setUniform3fv("lightColor[0]", lightColorArray, 0, 21);

        if(flipX){
            shader.setUniformi("xInvert", 1);
        }else{
            shader.setUniformi("xInvert", 0);
        }

        shader.setUniformi("yInvert", 0);
        shader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Color color = gameMap.getLightsMapLayer().getShaderAmbientLightColor();

        shader.setUniformf("ambientColor", color.r, color.g, color.b,gameMap.getLightsMapLayer().getShaderAmbientLightIntensity());




    }


}
