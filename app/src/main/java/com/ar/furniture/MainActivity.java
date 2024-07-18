package com.ar.furniture;

import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.*;
import android.view.*;

import com.google.ar.core.Anchor;
import com.google.ar.core.Session;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.rendering.ModelRenderable;

public class MainActivity extends AppCompatActivity {
    
    private ArFragment arFragment;
    private String currentGlb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView[] imgs = new ImageView[11];
        for(int i=0;i<imgs.length;i++)
            {
                final int iFinal = i;
                imgs[i] = findViewById(getResources().getIdentifier("img"+String.valueOf(i+1), "id", getApplicationContext().getPackageName()));
                imgs[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int j=0;j<imgs.length;j++)
                        {
                            imgs[j].setAlpha(127);
                        }
                        imgs[iFinal].setAlpha(255);
                        switch(iFinal)
                        {
                            case 0: currentGlb = "78-sofa.glb";break;
                            case 1: currentGlb = "53-sofa.glb";break;
                            case 2: currentGlb = "54-sofa.glb";break;
                            case 3: currentGlb = "wardrobe.glb";break;
                            case 4: currentGlb = "wardrobe1.glb";break;
                            case 5: currentGlb = "wardrobe2.glb";break;
                            case 6: currentGlb = "pink.glb";break;
                        }
                    }
                });
                imgs[i].setAlpha(127);
            }
            imgs[0].setAlpha(255);
            currentGlb = "78-sofa.glb";

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> 
        {
            try{
            Anchor anchor = hitResult.createAnchor();
            ModelRenderable.builder()
                .setSource(getApplicationContext(), Uri.parse(currentGlb))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(modelRenderable -> addModelToScene(anchor, modelRenderable));
            }catch(Exception e){Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();}
        });

        Button wallButton = findViewById(R.id.button_wall);
        wallButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {                        
                        Session session = arFragment.getArSceneView().getSession();
                        //float[] pos = { 0, 0, -1 };
                        //float[] rotation = { 0, 0, 0, 1 };
                        Anchor anchor =  session.createAnchor(arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().makeTranslation(0, 200, 0));
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
                        transformableNode.setParent(anchorNode);
                        ModelRenderable.builder()
                            .setSource(getApplicationContext(), Uri.parse(currentGlb))
                            .setIsFilamentGltf(true)
                            .setAsyncLoadEnabled(true)
                            .build()
                            .thenAccept(andyRenderable -> transformableNode.setRenderable(andyRenderable));
                        arFragment.getArSceneView().getScene().addChild(anchorNode);
                        transformableNode.select();
                    }
                });

        new CountDownTimer(10000, 10000) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                wallButton.setVisibility(View.VISIBLE);
            }

        }.start();

    }

    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable)
    {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
    }
}