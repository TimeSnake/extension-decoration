package de.timesnake.extension.decoration.heads;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.database.util.decoration.DbHead;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.UUID;

public class Head {

    private final String section;
    private final String name;
    private final String url;
    private ExItemStack item;

    public Head(DbHead head) {
        this.name = head.getName();
        this.section = head.getSection();
        this.url = "https://textures.minecraft.net/texture/" + head.getTag();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        if (propertyMap == null) {
            throw new IllegalStateException("Profile doesn't contain a property map");
        }
        byte[] encodedData = new Base64().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        propertyMap.put("textures", new Property("textures", new String(encodedData)));

        this.item = new ExItemStack(Material.PLAYER_HEAD);

        ItemMeta headMeta = this.item.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        Head.getField(headMetaClass, "profile", GameProfile.class, 0).set(headMeta, profile);
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        this.item.setItemMeta(headMeta);
    }

    public String getName() {
        return name;
    }

    public String getSection() {
        return section;
    }

    public String getUrl() {
        return url;
    }

    public ExItemStack getItem() {
        return item;
    }


    static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);

                // A function for retrieving a specific field value
                return new FieldAccessor<T>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public T get(Object target) {
                        try {
                            return (T) field.get(target);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access reflection.", e);
                        }
                    }

                    @Override
                    public void set(Object target, Object value) {
                        try {
                            field.set(target, value);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access reflection.", e);
                        }
                    }

                    @Override
                    public boolean hasField(Object target) {
                        // target instanceof DeclaringClass
                        return field.getDeclaringClass().isAssignableFrom(target.getClass());
                    }
                };
            }
        }

        // Search in parent classes
        if (target.getSuperclass() != null) return getField(target.getSuperclass(), name, fieldType, index);
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }

    public interface FieldAccessor<T> {
        /**
         * Retrieve the content of a field.
         *
         * @param target the target object, or NULL for a static field
         * @return the value of the field
         */
        T get(Object target);

        /**
         * Set the content of a field.
         *
         * @param target the target object, or NULL for a static field
         * @param value  the new value of the field
         */
        void set(Object target, Object value);

        /**
         * Determine if the given object has this field.
         *
         * @param target the object to test
         * @return TRUE if it does, FALSE otherwise
         */
        boolean hasField(Object target);
    }
}
