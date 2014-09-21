package com.kogitune.wearhttp;

import android.content.Context;

import java.lang.reflect.Method;

class JudgeAndroidWear {

    public static boolean isWear(Context context){
        String defaultCharacteristics = "";
        String characteristics = get(context, "ro.build.characteristics", defaultCharacteristics);
        return characteristics.contains("watch");
    }

    /**
     * from StackOverFlow http://stackoverflow.com/questions/2641111/where-is-android-os-systemproperties
     * Get the value for the given key.
     * @return if the key isn't found, return def if it isn't null, or an empty string otherwise
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    private static String get(Context context, String key, String def) throws IllegalArgumentException {

        String ret= def;

        try{

            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            //Parameters
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new String(def);

            ret= (String) get.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }

        return ret;

    }
}
