package com.myapps.toualbiamine.basketbusiness;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myapps.toualbiamine.basketbusiness.Common.Common;
import com.myapps.toualbiamine.basketbusiness.Interface.ItemClickListener;
import com.myapps.toualbiamine.basketbusiness.Model.Food;
import com.myapps.toualbiamine.basketbusiness.ViewHolder.MenuViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import java.util.UUID;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference foods;
    FirebaseRecyclerAdapter<Food, MenuViewHolder> adapter;

    FirebaseStorage storage;
    StorageReference storageReference;

    RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;

    MaterialEditText foodNameInput;
    MaterialEditText foodDescriptionInput;
    Button selectBtn;
    Button uploadBtn;

    Food newFood;
    Uri saveURI;
    private final int CHOOSE_IMAGE_REQ = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUploadImagePopup();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        recyclerMenu = (RecyclerView) findViewById(R.id.recyclerMenu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerMenu.setLayoutManager(layoutManager);

        loadMenu();
    }

    private void showUploadImagePopup() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Add new menu");
        alertDialog.setMessage("Please fill out this information");

        LayoutInflater inflater = this.getLayoutInflater();
        View addMenuLayout = inflater.inflate(R.layout.add_menu_layout, null);

        foodNameInput = addMenuLayout.findViewById(R.id.foodNameInput);
        foodDescriptionInput = addMenuLayout.findViewById(R.id.foodDescriptionInput);
        selectBtn = addMenuLayout.findViewById(R.id.selectBtn);
        uploadBtn = addMenuLayout.findViewById(R.id.uploadBtn);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(addMenuLayout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Set button.
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(newFood != null) {
                    foods.push().setValue(newFood);
                    Toast.makeText(Home.this, newFood.getName() + " added to menu", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void uploadImage() {
        if(saveURI != null) {
            final ProgressDialog loadingDialog = new ProgressDialog(this);
            loadingDialog.setMessage("Uploading...");
            loadingDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    loadingDialog.dismiss();
                    Toast.makeText(Home.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Set value for new food only if image was uploaded & we can use the download URL
                            newFood = new Food(foodNameInput.getText().toString(), uri.toString(), foodDescriptionInput.getText().toString(), Common.currentUser.getRestaurantID());
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                            Toast.makeText(Home.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            loadingDialog.setMessage("Uploaded" + progress + "%");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSE_IMAGE_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveURI = data.getData();
            selectBtn.setText("Image Selected!");
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_IMAGE_REQ);
    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Food, MenuViewHolder>(
                Food.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                foods.orderByChild("restaurantID").equalTo(Common.currentUser.getRestaurantID())        //SELECT * FROM foods WHERE restaurantID = currentUser.getRestaurantID
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Food food, int i) {
                menuViewHolder.menuName.setText(food.getName());
                Picasso.with(Home.this).load(food.getImage())
                        .into(menuViewHolder.menuImg);
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();     //Refreshes data if data changed.
        recyclerMenu.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_orders) {
            Intent ordersPage = new Intent(Home.this, OrderStatus.class);
            startActivity(ordersPage);
        }

//        if (id == R.id.nav_home) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_tools) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)) {
            showUpdatePopup(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        if(item.getTitle().equals(Common.DELETE)) {
            deleteFood(adapter.getRef(item.getOrder()).getKey()) ;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {
        foods.child(key).removeValue();
        Toast.makeText(Home.this, "Menu deleted", Toast.LENGTH_SHORT).show();
    }

    private void showUpdatePopup(final String key,  final Food item) {
        Log.e("TAG", "Called showUpload with key = " + key + " & Food description = " + item.getDescription());
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Update menu");
        alertDialog.setMessage("Please fill out this information");

        LayoutInflater inflater = this.getLayoutInflater();
        View addMenuLayout = inflater.inflate(R.layout.add_menu_layout, null);

        foodNameInput = addMenuLayout.findViewById(R.id.foodNameInput);
        foodNameInput.setText(item.getName());
        foodDescriptionInput = addMenuLayout.findViewById(R.id.foodDescriptionInput);
        foodDescriptionInput.setText(item.getDescription());        //For some reason, item.getDescription() => null. TO FIX in the future.
        selectBtn = addMenuLayout.findViewById(R.id.selectBtn);
        uploadBtn = addMenuLayout.findViewById(R.id.uploadBtn);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        alertDialog.setView(addMenuLayout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Set button.
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                newFood = new Food(foodNameInput.getText().toString(), item.getImage(), foodDescriptionInput.getText().toString(), Common.currentUser.getRestaurantID());
                foods.child(key).setValue(newFood);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void changeImage(final Food item) {
        if(saveURI != null) {
            final ProgressDialog loadingDialog = new ProgressDialog(this);
            loadingDialog.setMessage("Uploading...");
            loadingDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    loadingDialog.dismiss();
                    Toast.makeText(Home.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Change image for food object
                            item.setImage(uri.toString());
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                            Toast.makeText(Home.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            loadingDialog.setMessage("Uploaded" + progress + "%");
                        }
                    });
        }
    }

}


