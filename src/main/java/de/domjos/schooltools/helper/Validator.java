/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.helper;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.LinkedHashMap;
import java.util.Map;

import de.domjos.schooltools.R;

public class Validator {
    private Map<Integer, Boolean> states;
    private Context context;

    public Validator(Context context) {
        this.context = context;
        this.states = new LinkedHashMap<>();
    }

    public void addEmptyValidator(final EditText txt) {
        states.put(txt.getId(), false);
        txt.setError(String.format(this.context.getString(R.string.message_validation_empty), txt.getHint()));
        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                states.put(txt.getId(), !editable.toString().equals(""));
                if(editable.toString().equals("")) {
                    txt.setError(String.format(context.getString(R.string.message_validation_empty), txt.getHint()));
                } else {
                    txt.setError(null);
                }
            }
        });
    }

    public void addLengthValidator(final EditText txt, final int minLength, final int maxLength) {
        states.put(txt.getId(), txt.getText().length()<=maxLength && txt.getText().length()>=minLength);
        if(txt.getText().length()<=maxLength && txt.getText().length()>=minLength) {
            txt.setError(null);
        } else {
            txt.setError(String.format(this.context.getString(R.string.message_validator_length), txt.getHint(), maxLength, minLength));
        }

        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                states.put(txt.getId(), txt.getText().length()<=maxLength && txt.getText().length()>=minLength);
                if(txt.getText().length()<=maxLength && txt.getText().length()>=minLength) {
                    txt.setError(null);
                } else {
                    txt.setError(String.format(context.getString(R.string.message_validator_length), txt.getHint(), maxLength, minLength));
                }
            }
        });
    }

    public void addIntegerValidator(final EditText txt) {
        states.put(txt.getId(), false);
        txt.setError(String.format(this.context.getString(R.string.message_validation_integer), txt.getHint()));
        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    states.put(txt.getId(), true);
                    txt.setError(null);
                } catch (Exception ex) {
                    states.put(txt.getId(), false);
                    txt.setError(String.format(context.getString(R.string.message_validation_integer), txt.getHint()));
                }
            }
        });
    }

    public void addDoubleValidator(final EditText txt) {
        if(!txt.getText().toString().equals("")) {
            states.put(txt.getId(), false);
            txt.setError(String.format(this.context.getString(R.string.message_validation_double), txt.getHint()));
        }
        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")) {
                    try {
                        states.put(txt.getId(), true);
                        txt.setError(null);
                    } catch (Exception ex) {
                        states.put(txt.getId(), false);
                        txt.setError(String.format(context.getString(R.string.message_validation_double), txt.getHint()));
                    }
                } else {
                    states.put(txt.getId(), true);
                    txt.setError(null);
                }
            }
        });
    }

    public void addDateValidator(final EditText txt) {
        if(!txt.getText().toString().equals("")) {
            states.put(txt.getId(), false);
            txt.setError(String.format(this.context.getString(R.string.message_validation_date), txt.getHint()));
        }

        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")) {
                    try {
                        if(Converter.convertStringToDate(txt.getText().toString())==null) {
                            states.put(txt.getId(), false);
                            txt.setError(String.format(context.getString(R.string.message_validation_date), txt.getHint()));
                        } else {
                            states.put(txt.getId(), true);
                            txt.setError(null);
                        }
                    } catch (Exception ex) {
                        states.put(txt.getId(), false);
                        txt.setError(String.format(context.getString(R.string.message_validation_date), txt.getHint()));
                    }
                } else {
                    states.put(txt.getId(), true);
                    txt.setError(null);
                }
            }
        });
    }

    public boolean getState() {
        for(boolean state : states.values()) {
            if(!state) {
                return false;
            }
        }
        return true;
    }
}
