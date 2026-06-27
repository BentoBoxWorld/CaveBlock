package world.bentobox.caveblock;

public class WhiteBox {
    /**
     * Sets the value of a private static field using Java Reflection.
     * @param targetClass The class containing the static field.
     * @param fieldName The name of the private static field.
     * @param value The value to set the field to.
     */
    public static void setInternalState(Class<?> targetClass, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set static field '" + fieldName + "' on class " + targetClass.getName(), e);
        }
    }
}
