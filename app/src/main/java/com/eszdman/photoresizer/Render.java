package com.eszdman.photoresizer;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicResize;
import android.renderscript.Type;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.eszdman.photoresizer.MainActivity.instance;

public class Render {
    public static void ReSizeFile(File in, int TargetMP, int NeedMP){
        Bitmap imagebit =null;
        try {
            imagebit = MediaStore.Images.Media.getBitmap(instance.getContentResolver(), Uri.fromFile(in));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(imagebit == null) return;
        RenderScript rs = RenderScript.create(instance);
        ExifInterface oldexif = null;
        try {
           oldexif = new ExifInterface(in.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int mp = imagebit.getWidth()*imagebit.getHeight();
        double k = ((double)mp)/NeedMP;
        k = Math.sqrt(k);
        int dstWidth = (int)((double)(imagebit.getWidth())/k);
        //Toast.makeText(instance,"DstWidth:"+dstWidth,Toast.LENGTH_SHORT).show();
        Bitmap out = resizeBitmap2(rs,imagebit,TargetMP, dstWidth);
        FileOutputStream os = null;
        if(out != imagebit)
            try {
                os = new FileOutputStream(String.format(
                        in.getAbsolutePath(),
                        System.currentTimeMillis()));
                out.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();
            } catch (Exception e){
            }
        try {
            if(oldexif != null) oldexif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Bitmap computeBit(Bitmap bitmap){
        RenderScript rs = RenderScript.create(instance);
        Allocation allocation = Allocation.createFromBitmap(rs, bitmap);
        Type t = allocation.getType();
        Allocation blurredAllocation = Allocation.createTyped(rs, t);
        //Create script
        // ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //ScriptIntrinsicResize resize =  ScriptIntrinsicResize.create(rs);
        //Set blur radius (maximum 25.0)
        //blurScript.setRadius(radius);
        //Set input for script
        //resize.setInput();
        //blurScript.setInput(allocation);
        //Call script for output allocation
        //blurScript.forEach(blurredAllocation);
        //Copy script result into bitmap
        blurredAllocation.copyTo(bitmap);
        //Destroy everything to free memory
        allocation.destroy();
        blurredAllocation.destroy();
        //blurScript.destroy();
        t.destroy();
        return bitmap;
    }

    public static Bitmap resizeBitmap2(RenderScript rs, Bitmap src,int TargetMP, int dstWidth) {
        if(src ==null) return null;
        if(src.getWidth()*src.getHeight() >= TargetMP) {
            Bitmap.Config bitmapConfig = src.getConfig();
            int srcWidth = src.getWidth();
            int srcHeight = src.getHeight();
            float srcAspectRatio = (float) srcWidth / srcHeight;
            int dstHeight = (int) (dstWidth / srcAspectRatio);
            float resizeRatio = (float) srcWidth / dstWidth;
            /* Calculate gaussian's radius */
            float sigma = resizeRatio / (float) Math.PI;
            float radius = 2.0f * sigma - 1.5f;
            radius = Math.min(25, Math.max(0.0001f, radius));
            //Toast.makeText(instance,"radius:"+radius,Toast.LENGTH_SHORT).show();
            /* Gaussian filter */
            Allocation tmpIn = Allocation.createFromBitmap(rs, src);
            Allocation tmpFiltered = Allocation.createTyped(rs, tmpIn.getType());
            if(radius > 0.0001f) {
                ScriptIntrinsicBlur blurInstrinsic = ScriptIntrinsicBlur.create(rs, tmpIn.getElement());
                blurInstrinsic.setRadius(radius);
                blurInstrinsic.setInput(tmpIn);
                blurInstrinsic.forEach(tmpFiltered);
                tmpIn.destroy();
                blurInstrinsic.destroy();
            }
            else tmpFiltered = tmpIn;
            /* Resize */
            Bitmap dst = Bitmap.createBitmap(dstWidth, dstHeight, bitmapConfig);
            Type t = Type.createXY(rs, tmpFiltered.getElement(), dstWidth, dstHeight);
            Allocation tmpOut = Allocation.createTyped(rs, t);
            ScriptIntrinsicResize resizeIntrinsic = ScriptIntrinsicResize.create(rs);

            resizeIntrinsic.setInput(tmpFiltered);
            resizeIntrinsic.forEach_bicubic(tmpOut);
            tmpOut.copyTo(dst);

            tmpFiltered.destroy();
            tmpOut.destroy();
            resizeIntrinsic.destroy();

            return dst;
        } else return src;
    }
}
