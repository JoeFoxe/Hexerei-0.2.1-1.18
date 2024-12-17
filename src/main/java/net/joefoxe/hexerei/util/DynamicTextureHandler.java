package net.joefoxe.hexerei.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DynamicTextureHandler {
    public static FileToIdConverter TEXTURE_ID_CONVERTER = new FileToIdConverter("textures", ".png");

    public static Map<ResourceLocation, DynamicBaseSprite> textures = new HashMap<>();


    public static DynamicBaseSprite addNewSprite(ResourceLocation location, BlockState state) {
        TextureAtlasSprite sprite = getFirstSprite(state);
        if (sprite != null) {
            if (!textures.containsKey(location)) {

                try {
                    NativeImage image = sprite.contents().getOriginalImage();
                    AnimationMetadataSection metadata = getAnimationMetadata(sprite.contents().name());
                    FrameSize frameSize = metadata.calculateFrameSize(image.getWidth(), image.getHeight());
                    int scale = 2;
                    int width = 3;
                    int height = 2;
                    Tuple<NativeImage, Map<Direction, Integer>> tuple = createCubeTexture(state, frameSize, sprite, scale, width, height);
                    DynamicBaseSprite baseSprite = new DynamicBaseSprite(tuple.getA(), location, scale, width, height);
                    textures.put(location, baseSprite);
                    Minecraft.getInstance().getTextureManager().register(location, baseSprite);


//                    File file1 = new File(Minecraft.getInstance().gameDirectory, "screenshots");
//                    file1.mkdir();
//                    File file2;
//                    file2 = new File(file1, "test_screenshot.png");
//
//                    net.minecraftforge.client.event.ScreenshotEvent event = net.minecraftforge.client.ForgeHooksClient.onScreenshot(baseSprite.getPixels(), file2);
//
//                    final File target = event.getScreenshotFile();
//
//
//                    Util.ioPool().execute(() -> {
//                        try {
//                            baseSprite.getPixels().writeToFile(target);
//                            Component component = Component.literal(file2.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((p_168608_) -> {
//                                return p_168608_.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, target.getAbsolutePath()));
//                            });
//                            Minecraft.getInstance().gui.getChat().addMessage(component);
////                            if (event.getResultMessage() != null)
////                                pMessageConsumer.accept(event.getResultMessage());
////                            else
////                                pMessageConsumer.accept(Component.translatable("screenshot.success", component));
//                        } catch (Exception exception) {
////                            LOGGER.warn("Couldn't save screenshot", (Throwable)exception);
////                            pMessageConsumer.accept(Component.translatable("screenshot.failure", exception.getMessage()));
//                        } finally {
//                            baseSprite.getPixels().close();
//                        }
//
//                    });

//                    baseSprite.upload();
                    return baseSprite;


                }  catch(IOException err) {
//                    err.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Tuple<NativeImage, Map<Direction, Integer>> createCubeTexture(BlockState state, FrameSize frameSize, TextureAtlasSprite defaultSprite, int texScale, int width, int height) {
        TextureAtlasSprite sprite;
        List<BakedQuad> list;
        int col;
        Map<Direction, Integer> tintIndex = new HashMap<>();
        for (Direction dir : Direction.values())
            tintIndex.put(dir, -1);
        Direction dir;
        BakedModel model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);

        FrameSize oldFrameSize = frameSize;
        frameSize = new FrameSize(32, 32);


        //NativeImage originalImage,
        NativeImage newImage = new NativeImage(frameSize.width(), frameSize.height(), true);

        // Initialize the writing position
        int currentY = 0;

        int scale = (int)(frameSize.width() / 16f);
        int rotation = 0;

        // Rebuild and write the top face
        sprite = defaultSprite;
        dir = Direction.UP;
        list = model.getQuads(state, dir, RandomSource.create());
        if (list.size() > 0) {
            sprite = list.get(0).getSprite();
            rotation = detectRotation(normalizeUVs(getUVs(list.get(0).getVertices())));
            tintIndex.put(dir, list.get(0).getTintIndex());
        }
        list = model.getQuads(state, null, RandomSource.create());
        for (BakedQuad quad : list) {
            if (quad.getDirection() == dir) {
                sprite = quad.getSprite();
                rotation = detectRotation(normalizeUVs(getUVs(quad.getVertices())));
                tintIndex.put(dir, quad.getTintIndex());
            }
        }
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        col = tintIndex.get(dir) == -1 ? -1 : blockColors.getColor(state, null, null, tintIndex.get(dir));
        rebuildFace(firstFrameTexture(sprite.contents().getOriginalImage(), oldFrameSize), newImage, 0, currentY, scale, width, width, rotation, col);

        // Rebuild and write the bottom face
        rotation = 0;
        sprite = defaultSprite;
        dir = Direction.DOWN;
        list = model.getQuads(state, dir, RandomSource.create());
        if (list.size() > 0) {
            sprite = list.get(0).getSprite();
            rotation = detectRotation(normalizeUVs(getUVs(list.get(0).getVertices())));
            tintIndex.put(dir, list.get(0).getTintIndex());
        }
        list = model.getQuads(state, null, RandomSource.create());
        for (BakedQuad quad : list) {
            if (quad.getDirection() == dir) {
                sprite = quad.getSprite();
                rotation = detectRotation(normalizeUVs(getUVs(quad.getVertices())));
                tintIndex.put(dir, quad.getTintIndex());
            }
        }
        col = tintIndex.get(dir) == -1 ? -1 : Minecraft.getInstance().getBlockColors().getColor(state, null, null, tintIndex.get(dir));
        rebuildFace(firstFrameTexture(sprite.contents().getOriginalImage(), oldFrameSize), newImage, width * scale, currentY, scale, width, width, rotation, col);

        currentY += width * scale; // Move down to the next row

        rotation = 0;
        sprite = defaultSprite;
        list = model.getQuads(state, Direction.NORTH, RandomSource.create());
        if (list.size() > 0) {
            sprite = list.get(0).getSprite();
            rotation = detectRotation(normalizeUVs(getUVs(list.get(0).getVertices())));
            tintIndex.put(Direction.NORTH, list.get(0).getTintIndex());
        }
        col = tintIndex.get(Direction.NORTH) == -1 ? -1 : Minecraft.getInstance().getBlockColors().getColor(state, null, null, tintIndex.get(Direction.NORTH));
        rebuildFace(firstFrameTexture(sprite.contents().getOriginalImage(), oldFrameSize), newImage, 0, currentY, scale, height, width, rotation, col);

        rotation = 0;
        sprite = defaultSprite;
        list = model.getQuads(state, Direction.SOUTH, RandomSource.create());
        if (list.size() > 0) {
            sprite = list.get(0).getSprite();
            rotation = detectRotation(normalizeUVs(getUVs(list.get(0).getVertices())));
            tintIndex.put(Direction.SOUTH, list.get(0).getTintIndex());
        }
        col = tintIndex.get(Direction.SOUTH) == -1 ? -1 : Minecraft.getInstance().getBlockColors().getColor(state, null, null, tintIndex.get(Direction.SOUTH));
        rebuildFace(firstFrameTexture(sprite.contents().getOriginalImage(), oldFrameSize), newImage, height * scale, currentY, scale, height, width, rotation, col);

        currentY += width * scale; // Move down to the next row

        // Rebuild and write the left and right faces
        rotation = 0;
        sprite = defaultSprite;
        list = model.getQuads(state, Direction.WEST, RandomSource.create());
        if (list.size() > 0) {
            sprite = list.get(0).getSprite();
            rotation = detectRotation(normalizeUVs(getUVs(list.get(0).getVertices())));
            tintIndex.put(Direction.WEST, list.get(0).getTintIndex());
        }
        col = tintIndex.get(Direction.WEST) == -1 ? -1 : Minecraft.getInstance().getBlockColors().getColor(state, null, null, tintIndex.get(Direction.WEST));
        rebuildFace(firstFrameTexture(sprite.contents().getOriginalImage(), oldFrameSize), newImage, 0, currentY, scale, height, width, rotation, col);

        rotation = 0;
        sprite = defaultSprite;
        list = model.getQuads(state, Direction.EAST, RandomSource.create());
        if (list.size() > 0) {
            sprite = list.get(0).getSprite();
            rotation = detectRotation(normalizeUVs(getUVs(list.get(0).getVertices())));
            tintIndex.put(Direction.EAST, list.get(0).getTintIndex());
        }
        col = tintIndex.get(Direction.EAST) == -1 ? -1 : Minecraft.getInstance().getBlockColors().getColor(state, null, null, tintIndex.get(Direction.EAST));
        rebuildFace(firstFrameTexture(sprite.contents().getOriginalImage(), oldFrameSize), newImage, height * scale, currentY, scale, height, width, rotation, col);

        return new Tuple<>(newImage, tintIndex);
    }

    public static Vec2[] getUVs(int[] verts) {
        Vec2[] uvs = new Vec2[4];
        for (int i = 0; i < 4; i++) {

            float u = Float.intBitsToFloat(verts[(i * 8) + 4]);
            float v = Float.intBitsToFloat(verts[(i * 8) + 5]);

            uvs[i] = new Vec2(u, v);
        }
        return uvs;
    }
    public static Vec2[] normalizeUVs(Vec2[] uvs) {
        // Initialize min and max values
        float minU = Float.MAX_VALUE, maxU = Float.MIN_VALUE; float minV = Float.MAX_VALUE, maxV = Float.MIN_VALUE;
        Vec2[] returnUVs = new Vec2[4];
        // First loop to find the min and max values
        for (Vec2 uv : uvs) {
            if (uv.x < minU) minU = uv.x;
            if (uv.x > maxU) maxU = uv.x;
            if (uv.y < minV) minV = uv.y;
            if (uv.y > maxV) maxV = uv.y;
        } // Second loop to normalize the UVs
        for (int i = 0; i < uvs.length; i++) {
            float normalizedU = (uvs[i].x - minU) / (maxU - minU);
            float normalizedV = (uvs[i].y - minV) / (maxV - minV);
            returnUVs[i] = new Vec2(normalizedU, normalizedV);
        }
        return returnUVs;
    }


    public static int detectRotation(Vec2[] uvs) {
        Vec2[] rotation0 = {
                new Vec2(0.0f, 0.0f),
                new Vec2(0.0f, 1.0f),
                new Vec2(1.0f, 1.0f),
                new Vec2(1.0f, 0.0f)
        };

        Vec2[] rotation90 = {
                new Vec2(0.0f, 1.0f),
                new Vec2(1.0f, 1.0f),
                new Vec2(1.0f, 0.0f),
                new Vec2(0.0f, 0.0f)
        };

        Vec2[] rotation180 = {
                new Vec2(1.0f, 1.0f),
                new Vec2(1.0f, 0.0f),
                new Vec2(0.0f, 0.0f),
                new Vec2(0.0f, 1.0f)
        };

        Vec2[] rotation270 = {
                new Vec2(1.0f, 0.0f),
                new Vec2(0.0f, 0.0f),
                new Vec2(0.0f, 1.0f),
                new Vec2(1.0f, 1.0f)
        };

        if (matchUVs(uvs, rotation0)) return 0;
        if (matchUVs(uvs, rotation90)) return 90;
        if (matchUVs(uvs, rotation180)) return 180;
        if (matchUVs(uvs, rotation270)) return 270;

        return -1; // Unknown rotation
    }

    private static boolean matchUVs(Vec2[] uvs, Vec2[] rotation) {
        for (int i = 0; i < uvs.length; i++) {
            if (!uvs[i].equals(rotation[i])) {
                return false;
            }
        }
        return true;
    }


    public static void rebuildFace(NativeImage src, NativeImage dest, int destX, int destY, int scale, int sizeX, int sizeY, int rotation, int col) {


        int gridSizeX = Math.round(sizeX * scale);
        int gridSizeY = Math.round(sizeY * scale);
        int totalPoints = gridSizeX * gridSizeY;

        float centerX = (gridSizeX) / 2.0f;
        float centerY = (gridSizeY) / 2.0f;
        float[][] points = new float[totalPoints][2];
        for (int y = 0; y < gridSizeY; y++) {
            for (int x = 0; x < gridSizeX; x++) {
                points[y * gridSizeX + x] = new float[]{x, y};
                points[y * gridSizeX + x][0] /= Math.max(1, gridSizeX - 1);
                points[y * gridSizeX + x][1] /= Math.max(1, gridSizeY - 1);
            }
        }
//        int[][] offsets = { {0, -1}, {0, 1}, {-1, 0}, {1, 0} };
        for (float[] point : points) {
            int writeX = Mth.clamp(destX + Mth.floor(Mth.clamp(point[0] * (sizeX * scale), 0, (sizeX * scale) - 1)), 0, dest.getWidth() - 1);
            int writeY = Mth.clamp(destY + Mth.floor(Mth.clamp(point[1] * (sizeY * scale), 0, (sizeY * scale) - 1)), 0, dest.getHeight() - 1);
            float dx = 0.5f - point[0];
            float dy = 0.5f - point[1];
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            float offsetX = centerX / 2 * Math.max(0, 0.8f - dist) * dx;
            float offsetY = centerY / 2 * Math.max(0, 0.8f - dist) * dy;
            Vec2 uv1 = new Vec2(point[0], point[1]);
            Vec2 uv2 = rotateUV(uv1, rotation);


            float getXf = (Mth.clamp((src.getWidth()) * uv2.x + offsetX, 0, src.getWidth() - 1));
            float getYf = (Mth.clamp((src.getHeight()) * uv2.y + offsetY, 0, src.getHeight() - 1));
            int getX = Mth.clamp(Mth.floor(getYf), 0, src.getWidth() - 1);
            int getY = Mth.clamp(Mth.floor(getXf), 0, src.getHeight() - 1);
            int color = src.getPixelRGBA(getX, getY);

            color = mergeColors(col, color);

            float alpha = (color >> NativeImage.Format.RGBA.alphaOffset() & 255) / 255f;
            if (alpha > 0)
                dest.setPixelRGBA(writeX, writeY, color);
//            else {
//
//                for (int[] offset : offsets) {
//                    int newGetX = Mth.clamp(getX + offset[0], 0, src.getWidth() - 1);
//                    int newGetY = Mth.clamp(getY + offset[1], 0, src.getHeight() - 1);
//
//                    color = src.getPixelRGBA(newGetX, newGetY);
//
//                    color = mergeColors(col, color);
//
//                    alpha = (color >> NativeImage.Format.RGBA.alphaOffset() & 255) / 255f;
//                    if (alpha > 0) {
//                        dest.setPixelRGBA(writeX, writeY, color);
//                        break;
//                    }
//
//                }
//            }

        }

    }

    public static int mergeColors(int col, int color) {
        if (col == -1)
            return color;

        float r = (float) (col >> 16 & 255) / 255.0F;
        float g = (float) (col >> 8 & 255) / 255.0F;
        float b = (float) (col & 255) / 255.0F;

        float newR = (color >> NativeImage.Format.RGBA.redOffset() & 255) / 255f * r;
        float newG = (color >> NativeImage.Format.RGBA.greenOffset() & 255) / 255f * g;
        float newB = (color >> NativeImage.Format.RGBA.blueOffset() & 255) / 255f * b;

        float alpha = (color >> NativeImage.Format.RGBA.alphaOffset() & 255) / 255f;

        return HexereiUtil.getColorValueAlpha(newR, newG, newB, alpha);
    }

    public static Vec2 rotateUV(Vec2 uv, int angle) {
        // Convert angle from degrees to radians
        double radians = Math.toRadians(angle);
        // Translate UV to the origin
        float u = uv.x - 0.5f;
        float v = uv.y - 0.5f; // Apply the rotation
        float rotatedU = (float) (u * Math.cos(radians) - v * Math.sin(radians));
        float rotatedV = (float) (u * Math.sin(radians) + v * Math.cos(radians)); // Translate UV back to its original position
        rotatedU += 0.5f; rotatedV += 0.5f;
        return new Vec2(rotatedU, rotatedV);
    }


    public static NativeImage firstFrameTexture(NativeImage originalImage, FrameSize frameSize) {
        int width = frameSize.width();
        int height = frameSize.height();
        NativeImage newImage = new NativeImage(width, height, true);

        // Example modification: Invert colors
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = originalImage.getPixelRGBA(x, y);
//                int invertedColor = ~color | (color & 0xFF000000); // Invert color while keeping alpha
                newImage.setPixelRGBA(x, y, color);
            }
        }

        return newImage;
    }

    public static AnimationMetadataSection getAnimationMetadata(ResourceLocation textureLocation) throws IOException {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceManager resourceManager = minecraft.getResourceManager();
        Optional<Resource> optional = resourceManager.getResource(TEXTURE_ID_CONVERTER.idToFile(textureLocation));
        AnimationMetadataSection defaultMetadata = AnimationMetadataSection.EMPTY;
        if (optional.isPresent()) {
            Optional<AnimationMetadataSection> optional2 = optional.get().metadata().getSection(AnimationMetadataSection.SERIALIZER);
            if (optional2.isPresent())
                return optional2.get();
        }
        return defaultMetadata;
    }
    public static TextureAtlasSprite getFirstSprite(BlockState blockState) {
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel model = minecraft.getModelManager().getBlockModelShaper().getBlockModel(blockState);

        // Cycle through all directions first
        for (Direction direction : Direction.values()) {
            List<BakedQuad> quads = model.getQuads(blockState, direction, RandomSource.create());
            if (!quads.isEmpty()) {
                return quads.get(0).getSprite();
            }
        }

        // Then cycle through the unculled quads
        List<BakedQuad> unculledQuads = model.getQuads(blockState, null, RandomSource.create());
        if (!unculledQuads.isEmpty()) {
            return unculledQuads.get(0).getSprite();
        }

        return null; // Return null if no sprite is found
    }
    public static BakedQuad getFirstQuad(BlockState blockState) {
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel model = minecraft.getModelManager().getBlockModelShaper().getBlockModel(blockState);

        // Cycle through all directions first
        for (Direction direction : Direction.values()) {
            List<BakedQuad> quads = model.getQuads(blockState, direction, RandomSource.create());
            if (!quads.isEmpty()) {
                return quads.get(0);
            }
        }

        // Then cycle through the unculled quads
        List<BakedQuad> unculledQuads = model.getQuads(blockState, null, RandomSource.create());
        if (!unculledQuads.isEmpty()) {
            return unculledQuads.get(0);
        }

        return null; // Return null if no quad is found
    }


    public static class DynamicBaseSprite extends DynamicTexture {

        public ResourceLocation location;

        public float scale;
        public int width;
        public int height;

//        Map<Direction, Integer> tintIndex;


        public DynamicBaseSprite(NativeImage image, ResourceLocation location, float scale, int width, int height) {
            super(image);
            this.location = location;
            this.scale = scale;
            this.width = width;
            this.height = height;
//            this.tintIndex = tintIndex;
        }


    }

}