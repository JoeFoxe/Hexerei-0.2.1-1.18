package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTileEntities {

    public static DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Hexerei.MOD_ID);


    public static final RegistryObject<BlockEntityType<MixingCauldronTile>> MIXING_CAULDRON_TILE = TILE_ENTITIES.register(
            "mixing_cauldron_entity", () -> BlockEntityType.Builder.of(MixingCauldronTile::new, ModBlocks.MIXING_CAULDRON.get()).build(null));

    public static final RegistryObject<BlockEntityType<CofferTile>> COFFER_TILE = TILE_ENTITIES.register(
            "coffer_entity", () -> BlockEntityType.Builder.of(CofferTile::new, ModBlocks.COFFER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CourierPackageTile>> COURIER_PACKAGE_TILE = TILE_ENTITIES.register(
            "courier_package_tile", () -> BlockEntityType.Builder.of(CourierPackageTile::new, ModBlocks.COURIER_PACKAGE.get()).build(null));

    public static final RegistryObject<BlockEntityType<CourierLetterTile>> COURIER_LETTER_TILE = TILE_ENTITIES.register(
            "courier_letter_tile", () -> BlockEntityType.Builder.of(CourierLetterTile::new, ModBlocks.COURIER_LETTER.get()).build(null));


    public static final RegistryObject<BlockEntityType<HerbJarTile>> HERB_JAR_TILE = TILE_ENTITIES.register(
            "herb_jar_entity", () -> BlockEntityType.Builder.of(HerbJarTile::new, ModBlocks.HERB_JAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<CrystalBallTile>> CRYSTAL_BALL_TILE = TILE_ENTITIES.register(
            "crystal_ball_entity", () -> BlockEntityType.Builder.of(CrystalBallTile::new, ModBlocks.CRYSTAL_BALL.get()).build(null));

    public static final RegistryObject<BlockEntityType<BookOfShadowsAltarTile>> BOOK_OF_SHADOWS_ALTAR_TILE = TILE_ENTITIES.register(
            "book_of_shadows_altar_entity", () -> BlockEntityType.Builder.of(BookOfShadowsAltarTile::new, ModBlocks.BOOK_OF_SHADOWS_ALTAR.get(), ModBlocks.WILLOW_ALTAR.get(), ModBlocks.WITCH_HAZEL_ALTAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<BroomStandTile>> BROOM_STAND_TILE = TILE_ENTITIES.register(
            "broom_stand_entity", () -> BlockEntityType.Builder.of(BroomStandTile::new, ModBlocks.MAHOGANY_BROOM_STAND.get(), ModBlocks.MAHOGANY_BROOM_STAND_WALL.get(), ModBlocks.WILLOW_BROOM_STAND.get(), ModBlocks.WILLOW_BROOM_STAND_WALL.get(), ModBlocks.WITCH_HAZEL_BROOM_STAND.get(), ModBlocks.WITCH_HAZEL_BROOM_STAND_WALL.get()).build(null));

    public static final RegistryObject<BlockEntityType<OwlCourierDepotTile>> OWL_COURIER_DEPOT_TILE = TILE_ENTITIES.register(
            "owl_courier_depot_entity", () -> BlockEntityType.Builder.of(OwlCourierDepotTile::new, ModBlocks.MAHOGANY_COURIER_DEPOT.get(), ModBlocks.MAHOGANY_COURIER_DEPOT_WALL.get(), ModBlocks.WILLOW_COURIER_DEPOT.get(), ModBlocks.WILLOW_COURIER_DEPOT_WALL.get(), ModBlocks.WITCH_HAZEL_COURIER_DEPOT.get(), ModBlocks.WITCH_HAZEL_COURIER_DEPOT_WALL.get()).build(null));

    public static final RegistryObject<BlockEntityType<CandleTile>> CANDLE_TILE = TILE_ENTITIES.register(
            "candle_entity", () -> BlockEntityType.Builder.of(CandleTile::new, ModBlocks.CANDLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<CandleDipperTile>> CANDLE_DIPPER_TILE = TILE_ENTITIES.register(
            "candle_dipper_entity", () -> BlockEntityType.Builder.of(CandleDipperTile::new, ModBlocks.CANDLE_DIPPER.get()).build(null));

    public static final RegistryObject<BlockEntityType<DryingRackTile>> DRYING_RACK_TILE = TILE_ENTITIES.register(
            "drying_rack_entity", () -> BlockEntityType.Builder.of(DryingRackTile::new, ModBlocks.HERB_DRYING_RACK.get(), ModBlocks.MAHOGANY_DRYING_RACK.get(), ModBlocks.WILLOW_DRYING_RACK.get(), ModBlocks.WITCH_HAZEL_DRYING_RACK.get()).build(null));

    public static final RegistryObject<BlockEntityType<PestleAndMortarTile>> PESTLE_AND_MORTAR_TILE = TILE_ENTITIES.register(
            "pestle_and_mortar_entity", () -> BlockEntityType.Builder.of(PestleAndMortarTile::new, ModBlocks.PESTLE_AND_MORTAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<SageBurningPlateTile>> SAGE_BURNING_PLATE_TILE = TILE_ENTITIES.register(
            "sage_burning_plate_entity", () -> BlockEntityType.Builder.of(SageBurningPlateTile::new, ModBlocks.SAGE_BURNING_PLATE.get()).build(null));

    public static final RegistryObject<BlockEntityType<ModChestBlockEntity>> CHEST_TILE = TILE_ENTITIES.register(
            "chest_entity", () -> BlockEntityType.Builder.of(ModChestBlockEntity::new, ModBlocks.WILLOW_CHEST.get(), ModBlocks.WITCH_HAZEL_CHEST.get(), ModBlocks.MAHOGANY_CHEST.get()).build(null));

    public static final RegistryObject<BlockEntityType<ModSignBlockEntity>> SIGN_TILE = TILE_ENTITIES.register(
            "sign_entity", () -> BlockEntityType.Builder.of(ModSignBlockEntity::new, ModBlocks.WILLOW_SIGN.get(), ModBlocks.WITCH_HAZEL_SIGN.get(), ModBlocks.MAHOGANY_SIGN.get(), ModBlocks.WILLOW_WALL_SIGN.get(), ModBlocks.WITCH_HAZEL_WALL_SIGN.get(), ModBlocks.MAHOGANY_WALL_SIGN.get(), ModBlocks.POLISHED_WILLOW_SIGN.get(), ModBlocks.POLISHED_WITCH_HAZEL_SIGN.get(), ModBlocks.POLISHED_MAHOGANY_SIGN.get(), ModBlocks.POLISHED_WILLOW_WALL_SIGN.get(), ModBlocks.POLISHED_WITCH_HAZEL_WALL_SIGN.get(), ModBlocks.POLISHED_MAHOGANY_WALL_SIGN.get()).build(null));

    public static final RegistryObject<BlockEntityType<ModHangingSignBlockEntity>> HANGING_SIGN_TILE = TILE_ENTITIES.register(
            "hanging_sign_entity", () -> BlockEntityType.Builder.of(ModHangingSignBlockEntity::new, ModBlocks.MAHOGANY_HANGING_SIGN.get(), ModBlocks.MAHOGANY_WALL_HANGING_SIGN.get(), ModBlocks.WILLOW_HANGING_SIGN.get(), ModBlocks.WILLOW_WALL_HANGING_SIGN.get(), ModBlocks.WITCH_HAZEL_HANGING_SIGN.get(), ModBlocks.WITCH_HAZEL_WALL_HANGING_SIGN.get()).build(null));

    public static final RegistryObject<BlockEntityType<CuttingCrystalTile>> CUTTING_CRYSTAL_TILE = TILE_ENTITIES.register(
            "cutting_crystal_entity", () -> BlockEntityType.Builder.of(CuttingCrystalTile::new, ModBlocks.CUTTING_CRYSTAL.get()).build(null));

    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}
