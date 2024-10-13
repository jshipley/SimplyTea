package knightminer.simplytea.data.gen;

import knightminer.simplytea.SimplyTea;
import knightminer.simplytea.core.Registration;
import knightminer.simplytea.data.SimplyTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator extends ItemTagsProvider {
	public ItemTagGenerator(PackOutput output, BlockTagsProvider blockTags, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existing) {
		super(output, lookupProvider, blockTags.contentsGetter(), SimplyTea.MOD_ID, existing);
	}

	@Override
	public String getName() {
		return "Simply Tea Item Tags";
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(Tags.Items.RODS_WOODEN).add(Registration.tea_stick);
		this.tag(SimplyTags.Items.ICE_CUBES).add(Registration.ice_cube);
		this.tag(SimplyTags.Items.EXCLUSIVE_TEAS).add(
				Registration.cup_tea_green, Registration.cup_tea_black,
				Registration.cup_tea_iced, Registration.cup_tea_chai, Registration.cup_tea_chorus);
		this.tag(SimplyTags.Items.TEAS).add(Registration.cup_tea_floral).addTag(SimplyTags.Items.EXCLUSIVE_TEAS);

		this.tag(SimplyTags.Items.TEA_CROP).add(Registration.tea_leaf);

		// saplings
		copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
		// fences
		copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
		copy(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
		copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
	}
}
