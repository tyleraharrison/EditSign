package fr.raksrinana.editsign.forge.config.cloth;

import fr.raksrinana.editsign.forge.config.CommonConfig;
import fr.raksrinana.editsign.forge.config.Config;
import lombok.NoArgsConstructor;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.ModLoadingContext;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

@NoArgsConstructor
public class ClothConfigHook{
	private static final Pattern MINECRAFT_ID_PATTERN = Pattern.compile("#?[a-z0-9_.-]+:[a-z0-9/._-]+");
	
	public static Function<String, Optional<Component>> getMinecraftItemIdCellError(){
		return value -> {
			boolean valid;
			if(value == null || value.isEmpty()){
				valid = true;
			}
			else{
				valid = MINECRAFT_ID_PATTERN.matcher(value).matches();
			}
			
			if(!valid){
				return Optional.of(new TranslatableComponent("text.autoconfig.editsign.error.invalidItemResourceLocation"));
			}
			return Optional.empty();
		};
	}
	
	public void load(){
		ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((minecraft, screen) -> {
			ConfigBuilder builder = ConfigBuilder.create()
					.setParentScreen(screen)
					.setTitle(new TextComponent("EditSign"));
			
			fillConfigScreen(builder);
			
			return builder.build();
		}));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void fillConfigScreen(ConfigBuilder builder){
		CommonConfig config = Config.COMMON;
		
		StringListEntry reverseSneakingEntry = builder.entryBuilder()
				.startStrField(new TranslatableComponent(getFieldName("requiredItemId")), config.getRequiredItemStr())
				.setDefaultValue("")
				.setTooltip(getTooltips("requiredItemId", 4))
				.setSaveConsumer(config::setRequiredItemId)
				.setErrorSupplier(getMinecraftItemIdCellError())
				.build();
		
		ConfigCategory general = builder.getOrCreateCategory(new TranslatableComponent("text.autoconfig.editsign.category.default"));
		general.addEntry(reverseSneakingEntry);
	}
	
	private String getFieldName(String fieldName){
		return "text.autoconfig.editsign.option." + fieldName;
	}
	
	private Component[] getTooltips(String fieldName, int count){
		var tooltipKey = getFieldName(fieldName) + ".@Tooltip";
		var keys = new LinkedList<String>();
		if(count <= 1){
			keys.add(tooltipKey);
		}
		else{
			for(int i = 0; i < count; i++){
				keys.add(tooltipKey + "[" + i + "]");
			}
		}
		return keys.stream()
				.map(TextComponent::new)
				.toArray(Component[]::new);
	}
}
