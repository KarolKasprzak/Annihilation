//package com.cosma.annihilation.World;
//
//import box2dLight.DirectionalLight;
//import box2dLight.PointLight;
//import box2dLight.RayHandler;
//import com.badlogic.ashley.core.Engine;
//import com.badlogic.ashley.core.Entity;
//import com.badlogic.ashley.core.Family;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.maps.*;
//import com.badlogic.gdx.maps.tiled.TiledMap;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.*;
//import com.badlogic.gdx.utils.Array;
//import com.cosma.annihilation.Components.BodyComponent;
//import com.cosma.annihilation.Components.PlayerComponent;
//import com.cosma.annihilation.Entities.EntityFactory;
//import com.cosma.annihilation.Gui.Inventory.InventoryItemLocation;
//import com.cosma.annihilation.Utils.Enums.BodyID;
//import com.cosma.annihilation.Utils.CollisionID;
//import com.cosma.annihilation.Utils.Constants;
//
//import java.util.ArrayList;
//
//class WorldLoader {
//
//    private  Engine engine;
//    private  World world;
//    private  TiledMap gameMap;
//    private  RayHandler rayHandler;
//    private EntityFactory entityFactory;
//
//    public WorldLoader(Engine engine, World world, TiledMap gameMap, RayHandler rayHandler){
//        this.engine = engine;
//        this.world = world;
//        this.gameMap = gameMap;
//        this.rayHandler = rayHandler;
//        EntityFactory.getInstance().setEngine(engine);
//        EntityFactory.getInstance().setWorld(world);
//        entityFactory = EntityFactory.getInstance();
////        entityFactory.createPlayerEntity();
//        //Get player entity
//        loadMap();
//
//    }
//    void loadMap(){
//
////        //Player backlight
////        PointLight pl2 = new PointLight(rayHandler, 45, new Color(1,1,1,0.3f), 1.8f,0,0);
////        pl2.attachToBody(playerBody);
////        pl2.setIgnoreAttachedBody(true);
////        pl2.setStaticLight(false);
////        pl2.setXray(true);
////        pl2.setSoftnessLength(0.3f);
////        pl2.setSoft(true);
//                    System.out.println(gameMap.getLayers().size());
//                    MapLayers layers = gameMap.getLayers();
//                    MapLayer layer = layers.get("object");
//                    for(MapObject mo : layer.getObjects()){
//                        if("spawn".equals(mo.getName())){
//                            float[] dimension = getDimension(mo);
//                            Body playerBody = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first().getComponent(BodyComponent.class).body;
//                            playerBody.setTransform(dimension[0],dimension[1],0);
//                            continue;
//                        }
//                        if("spawn_door".equals(mo.getName())){
//                            float[] dimension = getDimension(mo);
//                            Entity entity = entityFactory.createDoorEntity();
//                            entity.getComponent(BodyComponent.class).body.setTransform(dimension[0],dimension[1],0);
//                            continue;
//                        }
//
//                        if("spawn_enemy".equals(mo.getName())){
//                            float[] dimension = getDimension(mo);
//                            Entity entity = entityFactory.createTestEnemy();
//                            entity.getComponent(BodyComponent.class).body.setTransform(dimension[0],dimension[1],0);
//
//                            continue;
//                        }
//                        if("ladder".equals(mo.getName())){
//                            float[] dimension = getDimension(mo);
//                            Body body = createLadder(dimension[0],dimension[1],dimension[2],dimension[3]);
//                            continue;
//                        }
//                        if("ground".equals(mo.getName())) {
//                            float[] dimension = getDimension(mo);
//                            Body body = createBoxBody(dimension[0], dimension[1], dimension[2], dimension[3], false, 0,dimension[4]);
//                            continue;
//                        }
//                        if("light".equals(mo.getName())){
//                            float[] dimension = getDimension(mo);
////                            ConeLight pl = new ConeLight(rayHandler, 128, new Color(1,1,1,1f), 5,dimension[0],dimension[1],-90,90);
////                            pl.setStaticLight(true);
////                            pl.setXray(true);
////                            pl.setSoft(true);
//                            return;
//                        }
//                        if("light1".equals(mo.getName())){
//                            float[] dimension = getDimension(mo);
//                            PointLight pl1 = new PointLight(rayHandler, 90, new Color(1,1,1,0.6f), 7,dimension[0],dimension[1]);
//                            pl1.setStaticLight(false);
//                            Filter filter = new Filter();
//                            filter.maskBits = CollisionID.CAST_SHADOW;
//                            pl1.setContactFilter(filter);
//                            pl1.setSoftnessLength(0.3f);
//                            pl1.setSoft(true);
//                            continue;
//                        }
//                        if("sun".equals(mo.getName())){
//                            float[] dimension = getDimension(mo);
//                            DirectionalLight sun = new DirectionalLight(rayHandler,80,new Color(1,1,1,1f),-91);
//                            Filter filter = new Filter();
//
//                            filter.maskBits = CollisionID.CAST_SHADOW;
//                            sun.setContactFilter(filter);
//                            sun.setSoft(true);
//                            sun.setSoftnessLength(0.4f);
//                            continue;
//                    }
//                        if("spawn_test".equals(mo.getName())){
//                            float[] dimension = getDimension(mo);
//                            BodyDef bodyDef = new BodyDef();
//                            bodyDef.type = BodyDef.BodyType.KinematicBody;
//                            bodyDef.position.set(dimension[0], dimension[1]);
//
//                            Body boxBody = world.createBody(bodyDef);
//                            PolygonShape shape = new PolygonShape();
//                            shape.setAsBox(dimension[2] / 2, dimension[3] / 2);
//                            //Fixture
//                            FixtureDef fixtureDef = new FixtureDef();
//                            fixtureDef.shape = shape;
//                            fixtureDef.density = 1f;
//                            boxBody.createFixture(fixtureDef);
//                            continue;
//                        }
//                        if("spawn_box".equals(mo.getName())){
//                            float[] dimension = getDimension(mo);
//                            entityFactory.createBoxEntity(dimension[0],dimension[1],getItemForContainer(mo));
////                            if(getItemForContainer(mo).size > 0)
////                            System.out.println(getItemForContainer(mo).get(0).getItemID());
//                            continue;
//                        }
//                    }
//                }
//
//        private Body createBoxBody(float x, float y, float width, float height,boolean dynamic,int selectType,float rotation) {
//                BodyDef bodyDef = new BodyDef();
//                if (dynamic) {
//                    bodyDef.type = BodyDef.BodyType.DynamicBody;
//                } else {
//                    bodyDef.type = BodyDef.BodyType.StaticBody;
//                }
//                if(rotation != 0){
//                      setRotatedPosition(bodyDef,x,y,width,height,rotation);
//                }else {
//                    bodyDef.position.set((width / 2) + x, (height / 2) + y);
//                }
//                Body boxBody = world.createBody(bodyDef);
//                PolygonShape shape = new PolygonShape();
//                shape.setAsBox(width / 2, height / 2);
//                //Fixture
//                FixtureDef fixtureDef = new FixtureDef();
//                fixtureDef.shape = shape;
//                fixtureDef.density = 1f;
//                fixtureDef.filter.categoryBits = CollisionID.SCENERY| CollisionID.CAST_SHADOW| CollisionID.JUMPABLE_OBJECT;
//                fixtureDef.filter.maskBits = -1;
//
//                switch (selectType) {
//                    case 0:
//                        boxBody.createFixture(fixtureDef).setUserData(BodyID.GROUND);
//
//
//
//                        break;
//
//                    case 1:
//                        fixtureDef.isSensor = true;
//                             break;
//
//                }
//                shape.dispose();
//                return boxBody;
//        }
//
//        private Body createLadder(float x, float y, float width, float height){
//                BodyDef bodyDef = new BodyDef();
//                bodyDef.type = BodyDef.BodyType.KinematicBody;
//                bodyDef.position.set((width / 2)+x ,(height / 2)+y);
//                Body ladderBody = world.createBody(bodyDef);
//                ladderBody.setUserData(height);
//                PolygonShape shape = new PolygonShape();
//                shape.setAsBox(width/2, height/2);
//                //Fixture
//                PolygonShape fshape = new PolygonShape();
//                fshape.setAsBox(width/2, height/2);
//                FixtureDef fixtureDef = new FixtureDef();
//                fixtureDef.shape = fshape;
//                fixtureDef.density = 1f;
//                fixtureDef.isSensor = true;
//                fixtureDef.filter.categoryBits = CollisionID.NO_SHADOW| CollisionID.JUMPABLE_OBJECT;
//                ladderBody.createFixture(fixtureDef).setUserData(BodyID.LADDER);
//                //Fixture sensor2
//                PolygonShape shapesensor2 = new PolygonShape();
//                shapesensor2.setAsBox(0.3f, 0.3f,new Vector2(0,(height / 2)-0.3f),0);
//                FixtureDef fixtureDef1 = new FixtureDef();
//                fixtureDef1.shape = shapesensor2;
//                fixtureDef1.isSensor = true;
//                fixtureDef1.filter.categoryBits = CollisionID.NO_SHADOW| CollisionID.JUMPABLE_OBJECT;;
//
//                ladderBody.createFixture(fixtureDef1).setUserData(BodyID.DESCENT_LADDER);
//                shape.dispose();
//                return ladderBody;
//
//        }
//
//        private void setRotatedPosition(BodyDef bodyDef,float x, float y, float width, float height,float rotation){
//            float DEGTORAD = 0.0174532925199432957f;
//            // Top left corner of object
//            Vector2 pos = new Vector2((x) , (y + height) );
//            // angle of rotation in radians
//            float angle = DEGTORAD * (rotation);
//            // half of diagonal for rectangular object
//            float radius = (float) ((Math
//                    .sqrt((width * width + height * height))) / 2f) ;
//            // Angle at diagonal of rectangular object
//            double theta = (Math.tanh(height / width) * DEGTORAD);
//
//            // Finding new position if rotation was with respect to top-left corner of object.
//            // X=x+ radius*cos(theta-angle)+(height/2)cos(90+angle)
//            // Y= y+radius*sin(theta-angle)-(height/2)sin(90+angle)
//            pos = pos.add((float) (radius * Math.cos(-angle + theta)),
//                    (float) (radius * Math.sin(-angle + theta))).add(
//                    (float) ((height  / 2) * Math.cos(90 * DEGTORAD
//                            + angle)),
//                    (float) (-(height  / 2) * Math.sin(90
//                            * DEGTORAD + angle)));
//            // transform the body
//            bodyDef.position.set(pos);
//            bodyDef.angle = -angle;
//        }
//
//        private Array<InventoryItemLocation> getItemForContainer(MapObject mapObject){
//            MapProperties properties = mapObject.getProperties();
//            Array<InventoryItemLocation> itemList = new Array<InventoryItemLocation>();
//            if (properties.containsKey("item1")) {
//                InventoryItemLocation item1 = new InventoryItemLocation();
//                item1.setItemID(properties.get("item1", String.class));
//                item1.setItemsAmount(properties.get("item1size", Integer.class));
//                item1.setTableIndex(0);
//                itemList.add(item1);
//            }
//            if (properties.containsKey("item2")) {
//                InventoryItemLocation item2 = new InventoryItemLocation();
//                item2.setItemID(properties.get("item2", String.class));
//                item2.setItemsAmount(properties.get("item2size", Integer.class));
//                item2.setTableIndex(1);
//                itemList.add(item2);
//            }
//            if (properties.containsKey("item3")) {
//                InventoryItemLocation item3 = new InventoryItemLocation();
//                item3.setItemID(properties.get("item3", String.class));
//                item3.setItemsAmount(properties.get("item3size", Integer.class));
//                item3.setTableIndex(2);
//                itemList.add(item3);
//            }
//            if (properties.containsKey("item4")) {
//                InventoryItemLocation item4 = new InventoryItemLocation();
//                item4.setItemID(properties.get("item4", String.class));
//                item4.setItemsAmount(properties.get("item4size", Integer.class));
//                item4.setTableIndex(3);
//                itemList.add(item4);
//            }
//            return itemList;
//        }
//
//        private float[] getDimension(MapObject mapObject){
//            MapProperties properties = mapObject.getProperties();
//            String[] dimension = {"x","y","width","height","rotation"};
//            float[] values = new float[dimension.length];
//
//            for(int i=0; i < dimension.length; i++) {
//                if (properties.containsKey(dimension[i])) {
//                    if (dimension[i].equals("rotation")) {
//                        values[i] = properties.get(dimension[i], Float.class);
//                    }else
//                       values[i] = properties.get(dimension[i], Float.class) / Constants.PPM;
//                }else values[i] = 0;
//            }
//            return values;
//        }
//
//}
