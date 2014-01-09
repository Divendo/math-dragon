package org.teaminfty.math_dragon.model;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.teaminfty.math_dragon.exceptions.ParseException;
import org.teaminfty.math_dragon.view.math.MathFactory;
import org.teaminfty.math_dragon.view.math.MathObject;
import org.w3c.dom.Document;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class FormulaDatabase extends SQLiteOpenHelper
{
    /** The database name */
    private static final String DATABASE_NAME = "formula_database";
    /** The database version */
    private static final int DATABASE_VERSION = 2;
    
    /** The information about the formulas table */
    public static final class TABLE_FORMULAS
    {
        /** The name of the table */
        public static final String NAME = "formulas";
        
        /** The ID of a formula */
        public static final String ID = "id";
        /** The name of the formula */
        public static final String FORMULA_NAME = "name";
        /** The date when formula was last changed */
        public static final String LAST_CHANGE = "last_change";
        /** An image of the formula (PNG format) */
        public static final String IMAGE = "img";
        /** The actual {@link MathObject} stored as a XML string */
        public static final String MATH_OBJECT = "math_object";
        
        /** The size of the formula image (in pixels) */
        public static final int IMAGE_SIZE = 64;
    }
    
    /** Represents a single row in the formulas table */
    public static class Formula
    {
        /** Constructor */
        public Formula(int id, String name, long lastChange, Bitmap bmp, MathObject mathObject)
        {
            this.id = id;
            this.name = name;
            this.lastChange = lastChange;
            this.bmp = bmp;
            this.mathObject = mathObject;
        }
        
        /** Constructor for construction from raw data
         * @param id The ID
         * @param name The name of the formula
         * @param datetime The last time the formula was changed as a string (format: yyyy-mm-dd hh:mm:ss)
         * @param bmp The bitmap as a byte array (PNG format)
         * @param xml The {@link MathObject} as a XML string */
        public Formula(int id, String name, String datetime, byte[] bmp, byte[] xml)
        {
            // Set the ID and name
            this.id = id;
            this.name = name;
            
            // Parse and set the last change date
            try
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                lastChange = format.parse(datetime).getTime();
            }
            catch(java.text.ParseException e)
            {
                lastChange = System.currentTimeMillis();
            }
            
            // Read the bitmap
            if(bmp != null)
                this.bmp = BitmapFactory.decodeByteArray(bmp, 0, bmp.length);

            // Read the XML
            if(xml != null)
            {
                try
                {
                    mathObject = MathFactory.fromXML(xml);
                }
                catch(ParseException e)
                {
                    // TODO Auto-generated catch block (when an error occurs during the conversion from the XML document to a MathObject)
                    e.printStackTrace();
                }
            }
        }
        
        /** The ID */
        public int id = 0;
        /** The name */
        public String name = "";
        /** The last time the formula was changed expressed in the number of milliseconds since 1970-01-01 GMT */
        public long lastChange = 0;
        /** The image of the formula (can be <tt>null</tt>) */
        public Bitmap bmp = null;
        /** The math object of the formula (can be <tt>null</tt>) */
        public MathObject mathObject = null;
    }
    
    /** Constructor */
    public FormulaDatabase(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Create a table for the formulas
        db.execSQL("CREATE TABLE " + TABLE_FORMULAS.NAME + " (" +
                        TABLE_FORMULAS.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        TABLE_FORMULAS.FORMULA_NAME + " TEXT NOT NULL," +
                        TABLE_FORMULAS.LAST_CHANGE + " TIMESTAMP NOT NULL," +
                        TABLE_FORMULAS.IMAGE + " BLOB NOT NULL," +
                        TABLE_FORMULAS.MATH_OBJECT + " BLOB NOT NULL" +
                   ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(oldVersion == 1 && newVersion == 2)
            upgradeV1toV2(db);
    }
    
    /** Upgrades the database from version 1 to version 2
     * @param db The database to upgrade */
    private void upgradeV1toV2(SQLiteDatabase db)
    {
        // Add the name column
        db.execSQL("ALTER TABLE " + TABLE_FORMULAS.NAME + " " +
                   "ADD COLUMN " + TABLE_FORMULAS.FORMULA_NAME + " TEXT NOT NULL AFTER " + TABLE_FORMULAS.ID);
    }

    /** Returns a list of all formulas in the database.
     * Each of the formulas have their {@link MathObject} set to <tt>null</tt>.
     * @return The list of formulas */
    public ArrayList<Formula> getAllFormulas()
    {
        // The list we're going to build
        ArrayList<Formula> out = new ArrayList<Formula>();
        
        // Open a connection to the database
        SQLiteDatabase db = getReadableDatabase();
        
        // Get a cursor for all formulas in the database and add all of them to our list
        Cursor cursor = db.query(TABLE_FORMULAS.NAME,
                new String[]{ TABLE_FORMULAS.ID, TABLE_FORMULAS.FORMULA_NAME, TABLE_FORMULAS.LAST_CHANGE, TABLE_FORMULAS.IMAGE },
                null, null, null, null, TABLE_FORMULAS.LAST_CHANGE + " DESC");
        while(cursor.moveToNext())
            out.add(new Formula(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getBlob(3), null));

        // Close the database connection
        db.close();
        
        // Return the created list
        return out;
    }

    /** Returns the formula with the given ID from the database.
     * If no formula with such an ID is found, <tt>null</tt> is returned.
     * Note that the result doesn't contain the bitmap of the formula.
     * @param id The ID of the formula to get.
     * @return The list of formulas */
    public Formula getFormulaByID(int id)
    {
        // Open a connection to the database
        SQLiteDatabase db = getReadableDatabase();
        
        // Get a cursor for the requested formula and check if it exists
        Cursor cursor = db.query(TABLE_FORMULAS.NAME,
                new String[]{ TABLE_FORMULAS.ID, TABLE_FORMULAS.FORMULA_NAME, TABLE_FORMULAS.LAST_CHANGE, TABLE_FORMULAS.MATH_OBJECT },
                TABLE_FORMULAS.ID + " = " + Integer.toString(id), null, null, null, null);
        if(cursor.getCount() == 0)
            return null;
        cursor.moveToFirst();
        
        // Close the database connection
        db.close();
        
        // Create a formula from the retrieved data and return it
        return new Formula(cursor.getInt(0), cursor.getString(1), cursor.getString(2), null, cursor.getBlob(3));
    }
    
    /** The ID that's used for inserting a formula into the database */
    public static final int INSERT_ID = 0;
    
    /** Save the {@link MathObject} as a formula with the given ID.
     * @param id The ID of the formula to overwrite, or {@link FormulaDatabase#INSERT_ID INSERT_ID} to create a new entry
     * @param name The name of the formula to save
     * @param mathObject The {@link MathObject} that is to be stored
     * @return Whether the formula was saved successfully or not
     */
    public boolean saveFormula(int id, String name, MathObject mathObject)
    {
        // Create a ContentValues instance we're going to pass to the database
        ContentValues values = new ContentValues(4);
        
        // Add the name to the ContentValues instance
        values.put(TABLE_FORMULAS.FORMULA_NAME, name);
        
        // Remember the default height of the MathObject and change its default height
        final int defHeight = mathObject.getDefaultHeight();
        mathObject.setDefaultHeight(TABLE_FORMULAS.IMAGE_SIZE);
        
        // Create a bitmap of the right size and create a canvas for it
        Bitmap bmp = Bitmap.createBitmap(TABLE_FORMULAS.IMAGE_SIZE, TABLE_FORMULAS.IMAGE_SIZE, Bitmap.Config.ARGB_8888);
        Rect bounding = mathObject.getBoundingBox();
        Canvas canvas = new Canvas(bmp);
        
        // Scale and translate the canvas so that the whole MathObject fits in
        final float scale = Math.min(1.0f, Math.min(((float) TABLE_FORMULAS.IMAGE_SIZE) / bounding.width(), ((float) TABLE_FORMULAS.IMAGE_SIZE) / bounding.height()));
        canvas.scale(scale, scale);
        canvas.translate((TABLE_FORMULAS.IMAGE_SIZE - bounding.width() * scale) / 2, (TABLE_FORMULAS.IMAGE_SIZE - bounding.height() * scale) / 2);
        
        // Draw the MathObject and reset its default height
        mathObject.draw(canvas);
        mathObject.setDefaultHeight(defHeight);
        
        // Put the bitmap in the ContentValues instance
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        values.put(TABLE_FORMULAS.IMAGE, byteStream.toByteArray());
        
        // Write the MathObject to a XML document
        try
        {
            // Convert the MathObject to a XML document
            Document doc = MathObject.createXMLDocument();
            mathObject.writeToXML(doc, doc.getDocumentElement());
            
            // Convert the XML document to a byte array and add it to the ContentValues instance
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            byteStream = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(doc), new StreamResult(byteStream));
            values.put(TABLE_FORMULAS.MATH_OBJECT, byteStream.toByteArray());
        }
        catch(ParserConfigurationException e)
        { return false; }
        catch(TransformerConfigurationException e)
        { /* Never thrown, ignore */ }
        catch(TransformerFactoryConfigurationError e)
        { return false; }
        catch(TransformerException e)
        { return false; }
        
        // Add the current date and time to the ContentValues
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        values.put(TABLE_FORMULAS.LAST_CHANGE, dateFormat.format(new Date(System.currentTimeMillis())));
        
        // Open a connection to the database
        SQLiteDatabase db = getWritableDatabase();
        
        // Determine if we've to insert or update a formula
        if(id == INSERT_ID)
        {
            // Insert the formula
            final long result = db.insert(TABLE_FORMULAS.NAME, null, values);

            // Close the database connection and return whether we've inserted the formula successfully
            db.close();
            return result != -1;
        }
        else
        {
            // Update the formula
            final long result = db.update(TABLE_FORMULAS.NAME, values, TABLE_FORMULAS.ID + " = " + Integer.toString(id), null);

            // Close the database connection and return whether we've inserted the formula successfully
            db.close();
            return result != 1;
        }
    }
}
