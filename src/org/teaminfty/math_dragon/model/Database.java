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
import org.teaminfty.math_dragon.view.math.ExpressionXMLReader;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Symbol;
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

public class Database extends SQLiteOpenHelper
{
    /** The database name */
    private static final String DATABASE_NAME = "formula_database";
    /** The database version */
    private static final int DATABASE_VERSION = 3;
    
    /** The information about the formulas table */
    public static final class TABLE_FORMULAS
    {
        /** The name of the table */
        public static final String NAME = "formulas";
        
        /** The ID of the formula */
        public static final String ID = "id";
        /** The name of the formula */
        public static final String FORMULA_NAME = "name";
        /** The date when formula was last changed */
        public static final String LAST_CHANGE = "last_change";
        /** An image of the formula (PNG format) */
        public static final String IMAGE = "img";
        /** The actual {@link Expression} stored as a XML string */
        public static final String MATH_OBJECT = "math_object";
        
        /** The size of the formula image (in pixels) */
        public static final int IMAGE_SIZE = 64;
    }
    
    /** Represents a single row in the formulas table */
    public static class Formula
    {
        /** Constructor */
        public Formula(int id, String name, long lastChange, Bitmap bmp, Expression mathObject)
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
         * @param xml The {@link Expression} as a XML string */
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
                    mathObject = ExpressionXMLReader.fromXML(xml);
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
        public Expression mathObject = null;
    }
    
    /** The information about the substitutions table */
    public static final class TABLE_SUBSTITUTIONS
    {
        /** The name of the table */
        public static final String NAME = "substitutions";

        /** The name of the variable that is to be substituted (saved as an integer in the format <tt>name - 'a'</tt>) */
        public static final String VAR_NAME = "var_name";
        /** A {@link MathSymbol} stored as a XML string that represents the value of the substitution */
        public static final String VALUE = "value";
    }
    
    /** Represents substitution */
    public static final class Substitution
    {
        /** Constructor */
        public Substitution(char name)
        {
            this.name = name;
            this.value = null;
        }
        
        /** Constructor */
        public Substitution(char name, Symbol value)
        {
            this.name = name;
            this.value = value;
        }
        
        /** Constructor for construction from raw data
         * @param name The name of the variable
         * @param xml The {@link MathSymbol} as a XML string */
        public Substitution(int name, byte[] xml)
        {
            // Set the name
            this.name = (char) (name + 'a');

            // Read the XML
            if(xml != null)
            {
                try
                {
                    Expression tmp = ExpressionXMLReader.fromXML(xml);
                    if(tmp instanceof Symbol)
                        value = (Symbol) tmp;
                }
                catch(ParseException e)
                {
                    // TODO Auto-generated catch block (when an error occurs during the conversion from the XML document to a MathObject)
                    e.printStackTrace();
                }
            }
        }
        
        /** The name of the variable to substitute */
        public char name = 'a';
        /** The value to substitute for the variable (<tt>null</tt> means no substitution) */
        public Symbol value = null;
    }
    
    /** Constructor */
    public Database(Context ctx)
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
        
        // Create a table for the substitutions
        db.execSQL("CREATE TABLE " + TABLE_SUBSTITUTIONS.NAME + " (" +
                        TABLE_SUBSTITUTIONS.VAR_NAME + " INTEGER NOT NULL PRIMARY KEY," +
                        TABLE_SUBSTITUTIONS.VALUE+ " BLOB NOT NULL" +
                   ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(oldVersion == 1 && newVersion >= 2)
            upgradeV1toV2(db);
        if(oldVersion <= 2 && newVersion >= 3)
            upgradeV2toV3(db);
    }
    
    /** Upgrades the database from version 1 to version 2
     * @param db The database to upgrade */
    private void upgradeV1toV2(SQLiteDatabase db)
    {
        // Add the name column
        db.execSQL("ALTER TABLE " + TABLE_FORMULAS.NAME + " " +
                   "ADD COLUMN " + TABLE_FORMULAS.FORMULA_NAME + " TEXT NOT NULL DEFAULT ''");
    }

    /** Upgrades the database from version 2 to version 3
     * @param db The database to upgrade */
    private void upgradeV2toV3(SQLiteDatabase db)
    {
        // Create a table for the substitutions
        db.execSQL("CREATE TABLE " + TABLE_SUBSTITUTIONS.NAME + " (" +
                        TABLE_SUBSTITUTIONS.VAR_NAME + " INTEGER NOT NULL PRIMARY KEY," +
                        TABLE_SUBSTITUTIONS.VALUE+ " BLOB NOT NULL" +
                   ")");
    }

    /** Returns a list of all formulas in the database.
     * Each of the formulas have their {@link Expression} set to <tt>null</tt>.
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
    
    /** Save the {@link Expression} as a formula with the given ID.
     * @param id The ID of the formula to overwrite, or {@link Database#INSERT_ID INSERT_ID} to create a new entry
     * @param name The name of the formula to save
     * @param mathObject The {@link Expression} that is to be stored
     * @return Whether the formula was saved successfully or not
     */
    public boolean saveFormula(int id, String name, Expression mathObject)
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
            Document doc = Expression.createXMLDocument();
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
            return result == 1;
        }
    }
    
    /** Returns an array containing all substitutions */
    public Substitution[] getAllSubstitutions()
    {
        // Open a connection to the database
        SQLiteDatabase db = getReadableDatabase();
        
        // Get a cursor for all substitutions in the database
        Cursor cursor = db.query(TABLE_SUBSTITUTIONS.NAME,
                new String[]{ TABLE_SUBSTITUTIONS.VAR_NAME, TABLE_SUBSTITUTIONS.VALUE },
                null, null, null, null, TABLE_SUBSTITUTIONS.VAR_NAME + " ASC");
        
        // Create an array large enough to contain all substitutions
        Substitution out[] = new Substitution[cursor.getCount()];
        
        // Add all substitutions to the array
        for(int i = 0; cursor.moveToNext(); ++i)
            out[i] = new Substitution(cursor.getInt(0), cursor.getBlob(1));

        // Close the database connection
        db.close();
        
        // Return the created list
        return out;
    }
    
    /** Returns the substitution for the given variable
     * @param varName The variable to check for
     * @return The substitution if it exists, <tt>null</tt> otherwise */
    public Substitution getSubstitution(char varName)
    {
        // Open a connection to the database
        SQLiteDatabase db = getReadableDatabase();
        
        // Get a cursor to check whether the requested substitution exists
        final int varNameInt = varName - 'a';
        Cursor cursor = db.query(TABLE_SUBSTITUTIONS.NAME, new String[]{ TABLE_SUBSTITUTIONS.VAR_NAME, TABLE_SUBSTITUTIONS.VALUE },
                TABLE_SUBSTITUTIONS.VAR_NAME + " = " + Integer.toString(varNameInt), null, null, null, null);
        
        // Check if the substitution exists
        if(!cursor.moveToFirst()) return null;
        
        // Construct and return the substitution
        return new Substitution(cursor.getInt(0), cursor.getBlob(1));
    }
    
    /** Returns whether a substitution for the given variable exists
     * @param varName The variable to check for
     * @return <tt>true</tt> if a substitution exists, <tt>false</tt> otherwise */
    public boolean substitutionExists(char varName)
    {
        // Open a connection to the database
        SQLiteDatabase db = getReadableDatabase();
        
        // Get a cursor to check whether the requested substitution exists
        final int varNameInt = varName - 'a';
        Cursor cursor = db.query(TABLE_SUBSTITUTIONS.NAME, new String[]{ TABLE_SUBSTITUTIONS.VAR_NAME },
                TABLE_SUBSTITUTIONS.VAR_NAME + " = " + Integer.toString(varNameInt), null, null, null, null);
        
        // Determine whether or not the substitution exists
        final boolean exists = cursor.getCount() != 0;
        
        // Close the database connection and return the result
        db.close();
        return exists;
    }
    
    /** Saves the given substitution
     * @param sub The substitution to save
     * @return <tt>true</tt> if the substitution was saved successful, <tt>false</tt> otherwise */
    public boolean saveSubstitution(Substitution sub)
    {
        // Get the variable name as an integer
        final int varName = sub.name - 'a';
        
        // Determine whether or not the substitution already exists
        final boolean subExists = substitutionExists(sub.name);
        
        // Check if we're deleting the substitution
        if(sub.value == null)
        {
            // If the substitution doesn't exist, we don't have anything to do
            if(!subExists) return true;
            
            // Open a connection to the database
            SQLiteDatabase db = getWritableDatabase();
            final int count = db.delete(TABLE_SUBSTITUTIONS.NAME, TABLE_SUBSTITUTIONS.VAR_NAME + " = " + Integer.toString(varName), null);
            db.close();
            return count != 0;
        }
        
        // Create a ContentValues instance we're going to pass to the database
        ContentValues values = new ContentValues(2);
        
        // Add the name to the ContentValues instance
        values.put(TABLE_SUBSTITUTIONS.VAR_NAME, varName);
        
        // Write the value to a XML document
        try
        {
            // Convert the MathObject to a XML document
            Document doc = Expression.createXMLDocument();
            sub.value.writeToXML(doc, doc.getDocumentElement());
            
            // Convert the XML document to a byte array and add it to the ContentValues instance
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(doc), new StreamResult(byteStream));
            values.put(TABLE_SUBSTITUTIONS.VALUE, byteStream.toByteArray());
        }
        catch(ParserConfigurationException e)
        { return false; }
        catch(TransformerConfigurationException e)
        { /* Never thrown, ignore */ }
        catch(TransformerFactoryConfigurationError e)
        { return false; }
        catch(TransformerException e)
        { return false; }
        
        // Open a connection to the database
        SQLiteDatabase db = getWritableDatabase();
        
        // Determine if we've to insert or update a formula
        if(subExists)
        {
            // Update the substitution
            final long result = db.update(TABLE_SUBSTITUTIONS.NAME, values, TABLE_SUBSTITUTIONS.VAR_NAME + " = " + Integer.toString(varName), null);

            // Close the database connection and return whether we've inserted the formula successfully
            db.close();
            return result == 1;
        }
        else
        {
            // Insert the substitution
            final long result = db.insert(TABLE_SUBSTITUTIONS.NAME, null, values);

            // Close the database connection and return whether we've inserted the formula successfully
            db.close();
            return result != -1;
        }
    }
}
