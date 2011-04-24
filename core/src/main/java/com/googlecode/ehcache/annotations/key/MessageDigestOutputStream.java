/**
 * Copyright 2010-2011 Nicholas Blair, Eric Dalquist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.ehcache.annotations.key;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;

/**
 * Simple OutputStream that writes all data directly to the configured
 * {@link MessageDigest} using the update methods.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class MessageDigestOutputStream extends OutputStream {
    private final DataOutputStream dataOutputStream;
    private final MessageDigest messageDigest;
    
    public MessageDigestOutputStream(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
        this.dataOutputStream = new DataOutputStream(this);
    }
    
    public MessageDigest getMessageDigest() {
        return this.messageDigest;
    }
    
    public final void writeBoolean(boolean v) {
        try {
            this.dataOutputStream.writeBoolean(v);
        }
        catch (IOException e) {
            //Ignore, not possible with this class
        }
    }

    public final void writeByte(int v) {
        try {
            this.dataOutputStream.writeByte(v);
        }
        catch (IOException e) {
            //Ignore, not possible with this class
        }
    }

    public final void writeChar(int v) {
        try {
            this.dataOutputStream.writeChar(v);
        }
        catch (IOException e) {
            //Ignore, not possible with this class
        }
    }

    public final void writeDouble(double v) {
        try {
            this.dataOutputStream.writeDouble(v);
        }
        catch (IOException e) {
            //Ignore, not possible with this class
        }
    }

    public final void writeFloat(float v) {
        try {
            this.dataOutputStream.writeFloat(v);
        }
        catch (IOException e) {
            //Ignore, not possible with this class
        }
    }

    public final void writeInt(int v) {
        try {
            this.dataOutputStream.writeInt(v);
        }
        catch (IOException e) {
            //Ignore, not possible with this class
        }
    }

    public final void writeLong(long v) {
        try {
            this.dataOutputStream.writeLong(v);
        }
        catch (IOException e) {
            //Ignore, not possible with this class
        }
    }

    public final void writeShort(int v) {
        try {
            this.dataOutputStream.writeShort(v);
        }
        catch (IOException e) {
            //Ignore, not possible with this class
        }
    }

    public final void writeUTF(String str) {
        try {
            this.dataOutputStream.writeUTF(str);
        }
        catch (IOException e) {
            //Ignore, not possible with this class
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        this.messageDigest.update(b, off, len);
    }

    @Override
    public void write(byte[] b) {
        this.messageDigest.update(b);
    }

    @Override
    public void write(int b) {
        this.messageDigest.update((byte)b);
    }
}
