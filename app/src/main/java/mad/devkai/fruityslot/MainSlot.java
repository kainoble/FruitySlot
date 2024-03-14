package mad.devkai.fruityslot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainSlot extends AppCompatActivity {
    private static final int MULTIPLY_BY_1 = 7;
    private static final int MULTIPLY_BY_2 = 72;
    private static final int MULTIPLY_BY_3 = 142;
    private static final int MULTIPLY_BY_4 = 212;
    private int addBy1 = 5;
    private int addBy2 = 5;
    private int addBy3 = 5;
    private final int[] slot = {1, 2, 3, 4, 5, 6, 7};

    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private RecyclerView recyclerView3;
    private CustomManager layout1;
    private CustomManager layout2;
    private CustomManager layout3;

    private TextView energyPrice;
    private TextView myPower;
    private TextView toPlay;

    int myCoinsVal;
    int playVal;
    int fortuneVal;

    private boolean firstRun;
    private boolean isSpinning = false;

    private HowToPlay gameLogic;

    private SharedPreferences prefs;
    public MediaPlayer musicPlayer;
    public MediaPlayer winSound;

    public MediaPlayer plusMinusSound;
    public MediaPlayer backgroundSound;
    public static final String PREFS_NAME = "FirstRun";

    private int playMusic;
    private int playSound;
    private ImageView musicOff;
    private ImageView musicOn;
    private ImageView soundOn;
    private ImageView soundOff;
    private boolean isAutoRotationEnabled = false;
    private ImageButton autoRotationButton;
    private ValueAnimator rotationAnimator;
    private int rotationCount = 0;
    private mad.devkai.fruityslot.HowToPlay HowToPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_slot);

        ImageButton minusButton;
        ImageButton plusButton;
        SpinnerAdapter adapter;
        ImageView settingsButton;
        ImageButton spinButton;

        backgroundSound = MediaPlayer.create(this, R.raw.background_music);
        backgroundSound.setLooping(true);
        musicPlayer = MediaPlayer.create(this, R.raw.dragon_spin);
        winSound = MediaPlayer.create(this, R.raw.won);
        plusMinusSound = MediaPlayer.create(this, R.raw.button);

        prefs = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        firstRun = prefs.getBoolean("firstRun", true);

        if (firstRun) {
            playMusic = 1;
            playSound = 1;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();
        } else {
            playMusic = prefs.getInt("music", 1);
            playSound = prefs.getInt("sound", 1);
            checkMusic();
        }

        gameLogic = new HowToPlay();
        settingsButton = findViewById(R.id.settings);
        spinButton = findViewById(R.id.spinButton);
        plusButton = findViewById(R.id.plusButton);
        minusButton = findViewById(R.id.minusButton);
        energyPrice = findViewById(R.id.fortuneprice);
        myPower = findViewById(R.id.energy);
        toPlay = findViewById(R.id.bet);
        adapter = new SpinnerAdapter(this, slot, HowToPlay);

        recyclerView1 = findViewById(R.id.spinner1);
        recyclerView2 = findViewById(R.id.spinner2);
        recyclerView3 = findViewById(R.id.spinner3);
        recyclerView1.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);
        recyclerView3.setHasFixedSize(true);

        layout1 = new CustomManager(this);
        layout1.setScrollEnabled(false);
        recyclerView1.setLayoutManager(layout1);
        layout2 = new CustomManager(this);
        layout2.setScrollEnabled(false);
        recyclerView2.setLayoutManager(layout2);
        layout3 = new CustomManager(this);
        layout3.setScrollEnabled(false);
        recyclerView3.setLayoutManager(layout3);

        recyclerView1.setAdapter(adapter);
        recyclerView2.setAdapter(adapter);
        recyclerView3.setAdapter(adapter);
        recyclerView1.scrollToPosition(addBy1);
        recyclerView2.scrollToPosition(addBy2);
        recyclerView3.scrollToPosition(addBy3);

        setText();
        updateText();

        recyclerView1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerView1.scrollToPosition(gameLogic.getPosition(0));
                    layout1.setScrollEnabled(false);
                }
            }
        });

        recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerView2.scrollToPosition(gameLogic.getPosition(1));
                    layout2.setScrollEnabled(false);
                }
            }
        });
        recyclerView3.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerView3.scrollToPosition(gameLogic.getPosition(2));
                    layout3.setScrollEnabled(false);
                    updateText();
                    if (gameLogic.getHasWon()) {
                        if (playSound == 1) {
                            winSound.start();
                        }
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.pop_up, findViewById(R.id.win_splash));
                        TextView winCoins = layout.findViewById(R.id.win_coins);
                        winCoins.setText(gameLogic.getPrize());
                        Toast toast = new Toast(MainSlot.this);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setView(layout);
                        toast.show();
                        gameLogic.setHasWon(false);
                    }
                    isSpinning = false;
                    spinButton.setEnabled(true);
                }
            }
        });

        spinButton.setOnClickListener(v -> {
            spinButton.setEnabled(false);
            if (!isSpinning) {
                if (playSound == 1) {
                    musicPlayer.start();
                }
                isSpinning = true;
            }
            layout1.setScrollEnabled(true);
            layout2.setScrollEnabled(true);
            layout3.setScrollEnabled(true);
            gameLogic.getSpinResults();
            addBy1 = gameLogic.getPosition(0) + MULTIPLY_BY_2;
            addBy2 = gameLogic.getPosition(1) + MULTIPLY_BY_3;
            addBy3 = gameLogic.getPosition(2) + MULTIPLY_BY_4;
            recyclerView1.smoothScrollToPosition(addBy1);
            recyclerView2.smoothScrollToPosition(addBy2);
            recyclerView3.smoothScrollToPosition(addBy3);

            startSpinAnimation(spinButton);
        });

        plusButton.setOnClickListener(v -> {
            if (playSound == 1) {
                plusMinusSound.start();
            }
            gameLogic.betUp();
            updateText();
        });

        minusButton.setOnClickListener(v -> {
            if (playSound == 1) {
                plusMinusSound.start();
            }
            gameLogic.betDown();
            updateText();
        });

        settingsButton.setOnClickListener(v -> {
            if (playSound == 1) {
                plusMinusSound.start();
            }
            showSettingsDialog();
        });
    }

    private void startSpinAnimation(View view) {
        Animation scaleAnimation = AnimationUtils.loadAnimation(MainSlot.this, R.anim.scale_animation);
        view.startAnimation(scaleAnimation);
    }

    private void setText() {
        if (firstRun) {
            gameLogic.setMyCoins(1000);
            gameLogic.setBet(5);
            gameLogic.setJackpot(100000);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();
        } else {
            String coins = prefs.getString("coins", "1000");
            String myPlay = prefs.getString("play", "5");
            String jackpot = prefs.getString("jackpot", "500000");
            Log.d("COINS", coins);
            myCoinsVal = Integer.parseInt(coins);
            playVal = Integer.parseInt(myPlay);
            fortuneVal = Integer.parseInt(jackpot);
            gameLogic.setMyCoins(myCoinsVal);
            gameLogic.setBet(playVal);
            gameLogic.setJackpot(fortuneVal);
        }
    }

    private void updateText() {
        energyPrice.setText(gameLogic.getJackpot());
        myPower.setText(gameLogic.getMyCoins());
        toPlay.setText(gameLogic.getBet());

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("coins", gameLogic.getMyCoins());
        editor.putString("play", gameLogic.getBet());
        editor.putString("jackpot", gameLogic.getJackpot());
        editor.apply();
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;

        public ItemViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.spinner_item);
        }
    }

    private class SpinnerAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        public SpinnerAdapter(MainSlot slotMachineActivity, int[] slot, HowToPlay gameLogic) {
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainSlot.this);
            View view = layoutInflater.inflate(R.layout.spin_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            int i = position < 7 ? position : position % MULTIPLY_BY_1;
            switch (slot[i]) {
                case 1:
                    holder.pic.setImageResource(R.drawable.choco);
                    break;
                case 2:
                    holder.pic.setImageResource(R.drawable.candy);
                    break;
                case 3:
                    holder.pic.setImageResource(R.drawable.star);
                    break;
                case 4:
                    holder.pic.setImageResource(R.drawable.pop);
                    break;
                case 5:
                    holder.pic.setImageResource(R.drawable.donut);
                    break;
                case 6:
                    holder.pic.setImageResource(R.drawable.orange);
                    break;
                case 7:
                    holder.pic.setImageResource(R.drawable.berry);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }

    private void showSettingsDialog() {
        final Dialog dialog;
        dialog = new Dialog(this, R.style.WinDialog);
        Objects.requireNonNull(dialog.getWindow()).setContentView(R.layout.activity_settings);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setCancelable(false);

        ImageView close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> dialog.dismiss());

        musicOn = dialog.findViewById(R.id.music_on);
        musicOn.setOnClickListener(v -> {
            playMusic = 0;
            checkMusic();
            musicOn.setVisibility(View.INVISIBLE);
            musicOff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("music", playMusic);
            editor.apply();
        });

        musicOff = dialog.findViewById(R.id.music_off);
        musicOff.setOnClickListener(v -> {
            playMusic = 1;
            backgroundSound.start();
            musicOn.setVisibility(View.VISIBLE);
            musicOff.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("music", playMusic);
            editor.apply();
        });

        soundOn = dialog.findViewById(R.id.sounds_on);
        soundOn.setOnClickListener(v -> {
            playSound = 0;
            checkSoundDraw();
            soundOn.setVisibility(View.INVISIBLE);
            soundOff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("sound", playSound);
            editor.apply();
        });

        soundOff = dialog.findViewById(R.id.sounds_off);
        soundOff.setOnClickListener(v -> {
            playSound = 1;
            soundOn.setVisibility(View.VISIBLE);
            soundOff.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("sound", playSound);
            editor.apply();
        });

        checkMusicDraw();
        checkSoundDraw();

        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        backgroundSound.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkMusic();
    }

    private void checkMusic() {
        if (playMusic == 1) {
            backgroundSound.start();
        } else {
            backgroundSound.pause();
        }
    }

    private void checkMusicDraw() {
        if (playMusic == 1) {
            musicOn.setVisibility(View.VISIBLE);
            musicOff.setVisibility(View.INVISIBLE);
        } else {
            musicOn.setVisibility(View.INVISIBLE);
            musicOff.setVisibility(View.VISIBLE);
        }
    }

    private void checkSoundDraw() {
        if (playSound == 1) {
            soundOn.setVisibility(View.VISIBLE);
            soundOff.setVisibility(View.INVISIBLE);
        } else {
            soundOn.setVisibility(View.INVISIBLE);
            soundOff.setVisibility(View.VISIBLE);
        }
    }
}