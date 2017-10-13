package com.seckill.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by tyd on 2017-9-28.
 */
public class ByteUtil {

    public static <T> T byteToObject(byte[] bytes, Class<T> t) throws IOException, ClassNotFoundException {

        T obj;

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        ObjectInputStream ois = new ObjectInputStream(bis);

        obj = (T) ois.readObject();

        bis.close();
        ois.close();

        return obj;
    }
}
