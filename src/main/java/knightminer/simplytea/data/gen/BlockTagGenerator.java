package knightminer.simplytea.data.gen;

import knightminer.simplytea.SimplyTea;
import knightminer.simplytea.core.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlockTagGenerator extends BlockTagsProvider {
	public BlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existing) {
		super(output, lookupProvider, SimplyTea.MOD_ID, existing);
	}

	@Override
	public String getName() {
		return "Simply Tea Block Tags";
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		// tea saplings
		this.tag(BlockTags.FLOWER_POTS).add(Registration.potted_tea_sapling);
		this.tag(BlockTags.SAPLINGS).add(Registration.tea_sapling);
		// tea fences
		this.tag(BlockTags.WOODEN_FENCES).add(Registration.tea_fence);
		this.tag(Tags.Blocks.FENCES_WOODEN).add(Registration.tea_fence);
		this.tag(BlockTags.FENCE_GATES).add(Registration.tea_fence_gate);
		this.tag(Tags.Blocks.FENCE_GATES_WOODEN).add(Registration.tea_fence_gate);

		this.tag(BlockTags.MINEABLE_WITH_AXE).add(Registration.tea_trunk, Registration.tea_fence, Registration.tea_fence_gate);
	}
}
