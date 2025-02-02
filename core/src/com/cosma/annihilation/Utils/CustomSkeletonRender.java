package com.cosma.annihilation.Utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.ShortArray;
import com.esotericsoftware.spine.BlendMode;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.ClippingAttachment;
import com.esotericsoftware.spine.attachments.MeshAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.esotericsoftware.spine.attachments.SkeletonAttachment;
import com.esotericsoftware.spine.utils.SkeletonClipping;

public class CustomSkeletonRender {
    static private final short[] quadTriangles = {0, 1, 2, 2, 3, 0};

    private boolean premultipliedAlpha;
    private final FloatArray vertices = new FloatArray(32);
    private final SkeletonClipping clipper = new SkeletonClipping();
    private com.esotericsoftware.spine.SkeletonRenderer.VertexEffect vertexEffect;
    private final Vector2 temp = new Vector2();
    private final Vector2 temp2 = new Vector2();
    private final Color temp3 = new Color();
    private final Color temp4 = new Color();
    private final Color temp5 = new Color();
    private final Color temp6 = new Color();

    public void draw (Batch batch, Skeleton skeleton,boolean drawNormal,Texture normal) {
        if (batch instanceof PolygonSpriteBatch) {
            draw((PolygonSpriteBatch) batch, skeleton, drawNormal,normal);
            return;
        }

        com.esotericsoftware.spine.SkeletonRenderer.VertexEffect vertexEffect = this.vertexEffect;
        if (vertexEffect != null) vertexEffect.begin(skeleton);

        boolean premultipliedAlpha = this.premultipliedAlpha;
        BlendMode blendMode = null;
        float[] vertices = this.vertices.items;
        Color skeletonColor = skeleton.getColor();
        float r = skeletonColor.r, g = skeletonColor.g, b = skeletonColor.b, a = skeletonColor.a;
        Array<Slot> drawOrder = skeleton.getDrawOrder();
        for (int i = 0, n = drawOrder.size; i < n; i++) {
            Slot slot = drawOrder.get(i);
            Attachment attachment = slot.getAttachment();
            if (attachment instanceof RegionAttachment) {
                RegionAttachment region = (RegionAttachment)attachment;
                region.computeWorldVertices(slot.getBone(), vertices, 0, 5);
                Color color = region.getColor(), slotColor = slot.getColor();
                float alpha = a * slotColor.a * color.a * 255;
                float multiplier = premultipliedAlpha ? alpha : 255;

                BlendMode slotBlendMode = slot.getData().getBlendMode();
                if (slotBlendMode != blendMode) {
                    if (slotBlendMode == BlendMode.additive && premultipliedAlpha) {
                        slotBlendMode = BlendMode.normal;
                        alpha = 0;
                    }
                    blendMode = slotBlendMode;
                    batch.setBlendFunction(blendMode.getSource(premultipliedAlpha), blendMode.getDest());
                }

                float c = NumberUtils.intToFloatColor(((int)alpha << 24) //
                        | ((int)(b * slotColor.b * color.b * multiplier) << 16) //
                        | ((int)(g * slotColor.g * color.g * multiplier) << 8) //
                        | (int)(r * slotColor.r * color.r * multiplier));
                float[] uvs = region.getUVs();
                for (int u = 0, v = 2; u < 8; u += 2, v += 5) {
                    vertices[v] = c;
                    vertices[v + 1] = uvs[u];
                    vertices[v + 2] = uvs[u + 1];
                }

                if (vertexEffect != null) applyVertexEffect(vertices, 20, 5, c, 0);

                batch.draw(region.getRegion().getTexture(), vertices, 0, 20);

            } else if (attachment instanceof ClippingAttachment) {
                clipper.clipStart(slot, (ClippingAttachment)attachment);
                continue;

            } else if (attachment instanceof MeshAttachment) {
                throw new RuntimeException(batch.getClass().getSimpleName()
                        + " cannot render meshes, PolygonSpriteBatch or TwoColorPolygonBatch is required.");

            } else if (attachment instanceof SkeletonAttachment) {
                Skeleton attachmentSkeleton = ((SkeletonAttachment)attachment).getSkeleton();
                if (attachmentSkeleton != null) draw(batch, attachmentSkeleton,false,null);
            }

            clipper.clipEnd(slot);
        }
        clipper.clipEnd();
        if (vertexEffect != null) vertexEffect.end();
    }

    @SuppressWarnings("null")
    public void draw (PolygonSpriteBatch batch, Skeleton skeleton, boolean drawNormal,Texture normal) {
        Vector2 tempPos = this.temp;
        Vector2 tempUv = this.temp2;
        Color tempLight = this.temp3;
        Color tempDark = this.temp4;
        Color temp5 = this.temp5;
        Color temp6 = this.temp6;
        com.esotericsoftware.spine.SkeletonRenderer.VertexEffect vertexEffect = this.vertexEffect;
        if (vertexEffect != null) vertexEffect.begin(skeleton);

        boolean premultipliedAlpha = this.premultipliedAlpha;
        BlendMode blendMode = null;
        int verticesLength = 0;
        float[] vertices = null, uvs = null;
        short[] triangles = null;
        Color color = null, skeletonColor = skeleton.getColor();
        float r = skeletonColor.r, g = skeletonColor.g, b = skeletonColor.b, a = skeletonColor.a;
        Array<Slot> drawOrder = skeleton.getDrawOrder();
        for (int i = 0, n = drawOrder.size; i < n; i++) {
            Slot slot = drawOrder.get(i);
            Texture texture = null;
            int vertexSize = clipper.isClipping() ? 2 : 5;
            Attachment attachment = slot.getAttachment();
            if (attachment instanceof RegionAttachment) {
                RegionAttachment region = (RegionAttachment)attachment;
                verticesLength = vertexSize << 2;
                vertices = this.vertices.items;
                region.computeWorldVertices(slot.getBone(), vertices, 0, vertexSize);
                triangles = quadTriangles;
                if(drawNormal){
                    texture = normal;
                }else{
                    texture = region.getRegion().getTexture();
                }

                uvs = region.getUVs();
                color = region.getColor();

            } else if (attachment instanceof MeshAttachment) {
                MeshAttachment mesh = (MeshAttachment)attachment;
                int count = mesh.getWorldVerticesLength();
                verticesLength = (count >> 1) * vertexSize;
                vertices = this.vertices.setSize(verticesLength);
                mesh.computeWorldVertices(slot, 0, count, vertices, 0, vertexSize);
                triangles = mesh.getTriangles();
                if(drawNormal){
                    texture = normal;
                }else{
                    texture = mesh.getRegion().getTexture();
                }

                uvs = mesh.getUVs();
                color = mesh.getColor();

            } else if (attachment instanceof ClippingAttachment) {
                ClippingAttachment clip = (ClippingAttachment)attachment;
                clipper.clipStart(slot, clip);
                continue;

            } else if (attachment instanceof SkeletonAttachment) {
                Skeleton attachmentSkeleton = ((SkeletonAttachment)attachment).getSkeleton();
                if (attachmentSkeleton != null) draw(batch, attachmentSkeleton,false,normal);
            }

            if (texture != null) {
                Color slotColor = slot.getColor();
                float alpha = a * slotColor.a * color.a * 255;
                float multiplier = premultipliedAlpha ? alpha : 255;

                BlendMode slotBlendMode = slot.getData().getBlendMode();
                if (slotBlendMode != blendMode) {
                    if (slotBlendMode == BlendMode.additive && premultipliedAlpha) {
                        slotBlendMode = BlendMode.normal;
                        alpha = 0;
                    }
                    blendMode = slotBlendMode;
                    batch.setBlendFunction(blendMode.getSource(premultipliedAlpha), blendMode.getDest());
                }

                float c = NumberUtils.intToFloatColor(((int)alpha << 24) //
                        | ((int)(b * slotColor.b * color.b * multiplier) << 16) //
                        | ((int)(g * slotColor.g * color.g * multiplier) << 8) //
                        | (int)(r * slotColor.r * color.r * multiplier));

                if (clipper.isClipping()) {
                    clipper.clipTriangles(vertices, verticesLength, triangles, triangles.length, uvs, c, 0, false);
                    FloatArray clippedVertices = clipper.getClippedVertices();
                    ShortArray clippedTriangles = clipper.getClippedTriangles();
                    if (vertexEffect != null) applyVertexEffect(clippedVertices.items, clippedVertices.size, 5, c, 0);
                    batch.draw(texture, clippedVertices.items, 0, clippedVertices.size, clippedTriangles.items, 0,
                            clippedTriangles.size);
                } else {
                    if (vertexEffect != null) {
                        temp5.set(NumberUtils.floatToIntColor(c));
                        temp6.set(0);
                        for (int v = 0, u = 0; v < verticesLength; v += 5, u += 2) {
                            tempPos.x = vertices[v];
                            tempPos.y = vertices[v + 1];
                            tempLight.set(temp5);
                            tempDark.set(temp6);
                            tempUv.x = uvs[u];
                            tempUv.y = uvs[u + 1];
                            vertexEffect.transform(tempPos, tempUv, tempLight, tempDark);
                            vertices[v] = tempPos.x;
                            vertices[v + 1] = tempPos.y;
                            vertices[v + 2] = tempLight.toFloatBits();
                            vertices[v + 3] = tempUv.x;
                            vertices[v + 4] = tempUv.y;
                        }
                    } else {
                        for (int v = 2, u = 0; v < verticesLength; v += 5, u += 2) {
                            vertices[v] = c;
                            vertices[v + 1] = uvs[u];
                            vertices[v + 2] = uvs[u + 1];
                        }
                    }
                    batch.draw(texture, vertices, 0, verticesLength, triangles, 0, triangles.length);
                }
            }

            clipper.clipEnd(slot);
        }
        clipper.clipEnd();
        if (vertexEffect != null) vertexEffect.end();
    }



    private void applyVertexEffect (float[] vertices, int verticesLength, int stride, float light, float dark) {
        Vector2 tempPos = this.temp;
        Vector2 tempUv = this.temp2;
        Color tempLight = this.temp3;
        Color tempDark = this.temp4;
        Color temp5 = this.temp5;
        Color temp6 = this.temp6;
        com.esotericsoftware.spine.SkeletonRenderer.VertexEffect vertexEffect = this.vertexEffect;
        temp5.set(NumberUtils.floatToIntColor(light));
        temp6.set(NumberUtils.floatToIntColor(dark));
        if (stride == 5) {
            for (int v = 0; v < verticesLength; v += stride) {
                tempPos.x = vertices[v];
                tempPos.y = vertices[v + 1];
                tempUv.x = vertices[v + 3];
                tempUv.y = vertices[v + 4];
                tempLight.set(temp5);
                tempDark.set(temp6);
                vertexEffect.transform(tempPos, tempUv, tempLight, tempDark);
                vertices[v] = tempPos.x;
                vertices[v + 1] = tempPos.y;
                vertices[v + 2] = tempLight.toFloatBits();
                vertices[v + 3] = tempUv.x;
                vertices[v + 4] = tempUv.y;
            }
        } else {
            for (int v = 0; v < verticesLength; v += stride) {
                tempPos.x = vertices[v];
                tempPos.y = vertices[v + 1];
                tempUv.x = vertices[v + 4];
                tempUv.y = vertices[v + 5];
                tempLight.set(temp5);
                tempDark.set(temp6);
                vertexEffect.transform(tempPos, tempUv, tempLight, tempDark);
                vertices[v] = tempPos.x;
                vertices[v + 1] = tempPos.y;
                vertices[v + 2] = tempLight.toFloatBits();
                vertices[v + 3] = tempDark.toFloatBits();
                vertices[v + 4] = tempUv.x;
                vertices[v + 5] = tempUv.y;
            }
        }
    }

    public boolean getPremultipliedAlpha () {
        return premultipliedAlpha;
    }

    public void setPremultipliedAlpha (boolean premultipliedAlpha) {
        this.premultipliedAlpha = premultipliedAlpha;
    }

    public com.esotericsoftware.spine.SkeletonRenderer.VertexEffect getVertexEffect () {
        return vertexEffect;
    }

    public void setVertexEffect (com.esotericsoftware.spine.SkeletonRenderer.VertexEffect vertexEffect) {
        this.vertexEffect = vertexEffect;
    }

    static public interface VertexEffect {
        public void begin (Skeleton skeleton);

        public void transform (Vector2 position, Vector2 uv, Color color, Color darkColor);

        public void end ();
    }
}

