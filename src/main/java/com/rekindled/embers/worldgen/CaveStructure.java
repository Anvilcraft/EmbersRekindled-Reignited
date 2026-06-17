package com.rekindled.embers.worldgen;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.RegistryManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

public class CaveStructure extends Structure {

	public static final MapCodec<CaveStructure> CODEC = RecordCodecBuilder.<CaveStructure>mapCodec(instance -> instance.group(
			settingsCodec(instance),
			StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
			ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
			Codec.intRange(0, 20).fieldOf("size").forGetter(structure -> structure.maxDepth),
			HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
			HeightProvider.CODEC.optionalFieldOf("max_height", ConstantHeight.of(VerticalAnchor.top())).forGetter(structure -> structure.maxHeight),
			Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
			Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
			Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
			Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(structure -> structure.poolAliases),
			DimensionPadding.CODEC.optionalFieldOf("dimension_padding", DimensionPadding.ZERO).forGetter(structure -> structure.dimensionPadding),
			LiquidSettings.CODEC.optionalFieldOf("liquid_settings", LiquidSettings.APPLY_WATERLOGGING).forGetter(structure -> structure.liquidSettings)
	).apply(instance, CaveStructure::new)).validate(CaveStructure::verifyRange);

	private final Holder<StructureTemplatePool> startPool;
	private final Optional<ResourceLocation> startJigsawName;
	private final int maxDepth;
	private final HeightProvider startHeight;
	private final HeightProvider maxHeight;
	private final boolean useExpansionHack;
	private final Optional<Heightmap.Types> projectStartToHeightmap;
	private final int maxDistanceFromCenter;
	private final List<PoolAliasBinding> poolAliases;
	private final DimensionPadding dimensionPadding;
	private final LiquidSettings liquidSettings;

	private static DataResult<CaveStructure> verifyRange(CaveStructure structure) {
		return structure.maxDistanceFromCenter > 128
				? DataResult.error(() -> "Structure size must not exceed 128")
				: DataResult.success(structure);
	}

	public CaveStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool,
			Optional<ResourceLocation> startJigsawName, int maxDepth, HeightProvider startHeight,
			HeightProvider maxHeight, boolean useExpansionHack, Optional<Heightmap.Types> projectStartToHeightmap,
			int maxDistanceFromCenter, List<PoolAliasBinding> poolAliases, DimensionPadding dimensionPadding,
			LiquidSettings liquidSettings) {
		super(settings);
		this.startPool = startPool;
		this.startJigsawName = startJigsawName;
		this.maxDepth = maxDepth;
		this.startHeight = startHeight;
		this.maxHeight = maxHeight;
		this.useExpansionHack = useExpansionHack;
		this.projectStartToHeightmap = projectStartToHeightmap;
		this.maxDistanceFromCenter = maxDistanceFromCenter;
		this.poolAliases = poolAliases;
		this.dimensionPadding = dimensionPadding;
		this.liquidSettings = liquidSettings;
	}

	public CaveStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, int maxDepth,
			HeightProvider startHeight, HeightProvider maxHeight, boolean useExpansionHack) {
		this(settings, startPool, Optional.empty(), maxDepth, startHeight, maxHeight, useExpansionHack,
				Optional.empty(), 80, List.of(), DimensionPadding.ZERO, LiquidSettings.APPLY_WATERLOGGING);
	}

	public CaveStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, int maxDepth,
			HeightProvider startHeight, HeightProvider maxHeight, boolean useExpansionHack, Heightmap.Types heightmap) {
		this(settings, startPool, Optional.empty(), maxDepth, startHeight, maxHeight, useExpansionHack,
				Optional.of(heightmap), 80, List.of(), DimensionPadding.ZERO, LiquidSettings.APPLY_WATERLOGGING);
	}

	@Override
	public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		ChunkPos chunk = context.chunkPos();
		int y = startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
		BlockPos start = new BlockPos(chunk.getMinBlockX(), y, chunk.getMinBlockZ());
		return JigsawPlacement.addPieces(context, startPool, startJigsawName, maxDepth, start, useExpansionHack,
				projectStartToHeightmap, maxDistanceFromCenter,
				PoolAliasLookup.create(poolAliases, start, context.seed()), dimensionPadding, liquidSettings);
	}

	@Override
	public StructureType<?> type() {
		return RegistryManager.CAVE_STRUCTURE.get();
	}
}
