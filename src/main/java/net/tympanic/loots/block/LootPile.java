package net.tympanic.loots.block;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.tympanic.loots.Main;
import net.tympanic.loots.screens.LootGenScreen;
import net.tympanic.loots.util.FileUtils;
import net.tympanic.loots.util.TickHandler;
import net.tympanic.loots.util.WeightedRandom;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;


public class LootPile extends Block {
    public static final IntProperty Tier = IntProperty.of("tier", 0, 2);
    public static final BooleanProperty Respawn = BooleanProperty.of("respawn");

    public static ArrayList<NbtCompound> Tier1 = new ArrayList<>();
    public static ArrayList<NbtCompound> Tier2 = new ArrayList<>();
    public static ArrayList<NbtCompound> Tier3 = new ArrayList<>();
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static ArrayList<NbtCompound> getTier(Integer num) {
        return (num == 0) ? Tier1 : (num == 1) ? Tier2 : (num == 2) ? Tier3 : new ArrayList<>();
    }

    public static void registerTiers() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path lootConfig = configDir.resolve("simple-loot-piles.json");
        if (Files.exists(configDir) && Files.isDirectory(configDir)) {
            if (Files.exists(lootConfig) && Files.isRegularFile(lootConfig)) {
                try {
                    String unparsedJson = FileUtils.read(lootConfig.toFile());
                    JsonObject json = gson.fromJson(unparsedJson, JsonObject.class);
                    for (int num = 0; num < 3; num++) {
                        JsonArray tier = json.getAsJsonArray(String.valueOf(num));
                        for (JsonElement element : tier) {
                            getTier(num).add(makeTierItem(Registries.ITEM.get(new Identifier(element.getAsJsonObject().get("item").getAsString())), element.getAsJsonObject().get("chance").getAsDouble(), FileUtils.jsonToNbt(element.getAsJsonObject().get("nbt").getAsJsonObject()), element.getAsJsonObject().get("amount").getAsInt()));
                        }
                    }
                } catch (JsonIOException e) {Main.LOGGER.error("Cannot parse json config file for simple loot piles");}
            } else {
                FileUtils.save(new File(configDir.toString(), lootConfig.getFileName().toString()), gson.toJson(JsonParser.parseString("{\"0\":[{\"item\":\"minecraft:dirt\",\"chance\":100.0,\"nbt\":{\"Enchantments\":[{\"id\":\"blast_protection\",\"lvl\":2}]},\"amount\":10}],\"1\":[],\"2\":[]}")));
            }
        } else Main.LOGGER.warn("no existing config folder found");
    }
    public static int calculateDropItem(ArrayList<NbtCompound> Tier) {
        WeightedRandom<Integer> Drops = new WeightedRandom<>();
        int num = 0;
        for (NbtCompound item : Tier) {
            Drops.addEntry(num, item.getDouble("chance"));
            num += 1;
        }
        int item = Drops.getRandom();
        return item;
    }

    private static NbtCompound makeTierItem(Item item, double chance, NbtCompound nbt, int amount) {
        NbtCompound thing = new NbtCompound();
        thing.putString("item", Registries.ITEM.getId(item).toString());
        thing.put("nbt", nbt);
        thing.putDouble("chance", chance);
        thing.putInt("amount", amount);
        return thing;
    }

    private static void dropItem(World world, BlockPos pos, ArrayList<NbtCompound> Tier, @Nullable Integer index) {
        if (index != null) {
            ItemStack item = new ItemStack(Registries.ITEM.get(new Identifier(Tier.get(index).getString("item"))));
            item.setCount(Tier.get(index).getInt("amount"));
            if (!Tier.get(index).getCompound("nbt").isEmpty()) {
                item.setNbt(Tier.get(index).getCompound("nbt"));
            }
            ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), item);
            world.spawnEntity(entity);
        }
    }

    public LootPile(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Tier, 0));
        setDefaultState(getDefaultState().with(Respawn, false));
    }

    @Override
    public void afterBreak(World w, PlayerEntity player, BlockPos p, BlockState b, @Nullable BlockEntity blockEntity, ItemStack tool) {
        switch (b.get(Tier)) {
            default -> dropItem(w, p, Tier1, calculateDropItem(Tier1));
            case 1 -> dropItem(w, p, Tier2, calculateDropItem(Tier2));
            case 2 -> dropItem(w, p, Tier3, calculateDropItem(Tier3));
        }
        if (b.get(Respawn)) TickHandler.create(100, () -> w.setBlockState(p, b));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.hasPermissionLevel(2)) {
            MinecraftClient.getInstance().execute(() -> {
                LootGenScreen screen = new LootGenScreen(Text.of("LootPile's State"), state, pos, world);
                MinecraftClient.getInstance().setScreen(screen);
            });
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 0.2f, 1f);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Tier);
        builder.add(Respawn);
    }
}
