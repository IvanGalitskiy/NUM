package com.vallsoft.num.ui.profile

import android.Manifest
import android.app.Activity
import android.app.Fragment
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.squareup.picasso.Picasso
import com.vallsoft.num.R
import com.vallsoft.num.data.UserCache
import com.vallsoft.num.data.database.UserPreference
import com.vallsoft.num.domain.utils.User
import com.vallsoft.num.presentation.profile.ProfilePresenter
import com.vallsoft.num.presentation.profile.ProfilePresenterImpl
import com.vallsoft.num.ui.MainActivity
import com.vallsoft.num.ui.masked_edit_text.MaskedEditText
import io.reactivex.subjects.PublishSubject
import java.io.File
import java.io.IOException


class ProfileFragment : Fragment(), View.OnClickListener, ProfilePresenter.View {



    private var activity: MainActivity? = null
    private var avatar_image: ImageView? = null
    private var phone_number_view: TextView? = null
    private var edit_btn: Button? = null
    private var save_btn: Button? = null
    private var cancel_btn: Button? = null
    private var name_field: EditText? = null
    private var address_field: EditText? = null
    private var country_field: AutoCompleteTextView? = null
    private var region_field: AutoCompleteTextView? = null
    private var operator_field: AutoCompleteTextView? = null
    private var category_field: AutoCompleteTextView? = null
    private var group_field: AutoCompleteTextView? = null


    private var vPhoneInput: MaskedEditText? = null
    private var vConfirmationCodeInput: EditText? = null
    private var vPhoneBtn: CardView? = null
    private var vBackToPhone: CardView? = null
    private var vResendCodeLbl: TextView? = null
    private var vEditPhoneBtn: ImageView? = null
    private var vSignInText: TextView? = null
    private var vPhoneTitle: TextView? = null
    private var vProgressBar: ProgressBar? = null
    private var vBackToProfile: ImageView? =null
    private var vRemoveProfile: TextView? =null


    private var vProfileContent: ConstraintLayout? = null
    private var vSyncPhone: ConstraintLayout? = null

    private var presenter: ProfilePresenter.Presenter? = null

    private var user: User? = null


    private var imageFile: File? = null
    private val PHOTO_REQUEST = 2
    private var mCameraOutputFile: Uri? = null


    private val timerStopSubject = PublishSubject.create<Boolean>()
    private var isTimerStopped = false
        set(value) {
            field = value
            timerStopSubject.onNext(field)
        }


    companion object {
        var viewState: ProfileViewState = ProfileViewState.Profile(false)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_profile, container, false)

        activity = getActivity() as MainActivity

        avatar_image = v.findViewById(R.id.avatar_image_view)

        // Buttons initialization
        edit_btn = v.findViewById(R.id.profile_edit_btn)
        cancel_btn = v.findViewById(R.id.profile_cancel_btn)
        save_btn = v.findViewById(R.id.profile_save_btn)

        // Fields initializations
        name_field = v.findViewById(R.id.name_field)
        address_field = v.findViewById(R.id.address_field)
        country_field = v.findViewById(R.id.country_field)
        region_field = v.findViewById(R.id.region_field)
        operator_field = v.findViewById(R.id.operator_field)
        category_field = v.findViewById(R.id.category_field)
        group_field = v.findViewById(R.id.group_field)
        phone_number_view = v.findViewById(R.id.phone_number_view)

        vPhoneBtn = v.findViewById(R.id.fragment_profile_phone_btn)
        vPhoneInput = v.findViewById(R.id.fragment_profile_sync_phone_input)
        vConfirmationCodeInput = v.findViewById(R.id.fragment_profile_sync_phone_code)
        vProfileContent = v.findViewById(R.id.fragment_profile_content)
        vSyncPhone = v.findViewById(R.id.fragment_profile_synd_phone_container)
        vResendCodeLbl = v.findViewById(R.id.fragment_profile_resend_code_lbl)
        vEditPhoneBtn = v.findViewById(R.id.fragment_profile_phone_edit)
        vPhoneTitle = v.findViewById(R.id.fragment_profile_phone_title)
        vBackToPhone = v.findViewById(R.id.fragment_profile_sync_back_to_phone)
        vSignInText = v.findViewById(R.id.fragment_phone_sync_sign_in_btn_text)
        vProgressBar = v.findViewById(R.id.vProgressBar)
        vBackToProfile = v.findViewById(R.id.fragment_profile_sync_back_to_profile)
        vRemoveProfile = v.findViewById(R.id.fragment_profile_remove_profile)

        vConfirmationCodeInput!!.addTextChangedListener(CodeTextWatcher())
        // обработчики событий
        edit_btn!!.setOnClickListener(this)
        save_btn!!.setOnClickListener(this)
        cancel_btn!!.setOnClickListener(this)
        avatar_image!!.setOnClickListener(this)
        vPhoneBtn!!.setOnClickListener(this)
        vEditPhoneBtn!!.setOnClickListener(this)
        vResendCodeLbl!!.setOnClickListener(this)
        vBackToProfile!!.setOnClickListener(this)
        vBackToPhone!!.setOnClickListener(this)
        vRemoveProfile!!.setOnClickListener(this)
        setFieldsInputType(InputType.TYPE_NULL)

        presenter = ProfilePresenterImpl(UserPreference(getActivity()), UserCache.instance)
        presenter!!.attachView(this)
        presenter!!.getPhoneNumber()
        switchToState(viewState)
        initTimerListener()

        return v
    }
    override fun showProgress() {
        vProgressBar!!.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        vProgressBar!!.visibility = View.GONE
    }
    fun initTimerListener() {
        timerStopSubject
                .subscribe {
                    vResendCodeLbl!!.isEnabled = it
                    if (viewState is ProfileViewState.SmsCode &&
                            !(viewState as ProfileViewState.SmsCode).isCodeInput) {
                        vPhoneBtn!!.isEnabled = it
                    } else {
                        vPhoneBtn!!.isEnabled = true
                    }
                }
    }

    private fun switchToState(state: ProfileViewState) {
        when (state) {
            is ProfileViewState.Profile -> {
                vProfileContent!!.visibility = View.VISIBLE
                vSyncPhone!!.visibility = View.INVISIBLE
                if (state.editing) {
                    save_btn!!.visibility = View.VISIBLE
                    cancel_btn!!.visibility = View.VISIBLE
                    edit_btn!!.visibility = View.INVISIBLE
                    setFieldsInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE)
                    vEditPhoneBtn!!.visibility = View.VISIBLE
                } else {
                    save_btn!!.visibility = View.INVISIBLE
                    cancel_btn!!.visibility = View.INVISIBLE
                    edit_btn!!.visibility = View.VISIBLE
                    setFieldsInputType(InputType.TYPE_NULL)
                    if (view != null)
                        hideKeyboard(view)
                    vEditPhoneBtn!!.visibility = View.GONE

                }
            }
            is ProfileViewState.SmsCode -> {
                vProfileContent!!.visibility = View.INVISIBLE
                vSyncPhone!!.visibility = View.VISIBLE
                if (state.isCodeInput) {
                    vPhoneTitle?.text = getString(R.string.input_code_from_sms)
                    vPhoneInput!!.visibility = View.GONE
                    vConfirmationCodeInput!!.visibility = View.VISIBLE
                    vResendCodeLbl?.visibility = View.VISIBLE
                    vResendCodeLbl?.isEnabled = false
                    vBackToPhone!!.visibility = View.VISIBLE
                } else {
                    vPhoneInput!!.visibility = View.VISIBLE
                    vConfirmationCodeInput!!.visibility = View.GONE
                    vPhoneTitle?.text = getString(R.string.input_your_phone)
                    vBackToPhone!!.visibility = View.INVISIBLE
                }
            }
        }
        viewState = state
    }


    override fun onStop() {
        presenter!!.detachView()
        super.onStop()
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.profile_edit_btn -> {
                switchToState(ProfileViewState.Profile(true))
            }
            R.id.profile_save_btn -> {
                switchToState(ProfileViewState.Profile(false))
                readUser()
                presenter!!.updateUser(user)
            }
            R.id.profile_cancel_btn -> {
                switchToState(ProfileViewState.Profile(false))
                displayUser(user)
            }
            R.id.avatar_image_view -> {
                if (viewState is ProfileViewState.Profile && (viewState as ProfileViewState.Profile).editing) {
                    openPhoto(PHOTO_REQUEST)
                }
            }
            R.id.fragment_profile_phone_btn ->
                if (viewState is ProfileViewState.SmsCode) {
                    if ((viewState as ProfileViewState.SmsCode).isCodeInput) {
                        presenter!!.updatePhoneWithSmsCode(vConfirmationCodeInput!!.text.toString())
                    } else {
                        if (vPhoneInput!!.isPhoneNumberValid) {
                            presenter!!.updatePhone(vPhoneInput!!.text.toString(), getActivity())
                        } else {
                            vPhoneInput!!.error = getString(R.string.invalid_phone)
                        }
                    }
                }
            R.id.fragment_profile_phone_edit -> {
                switchToState(ProfileViewState.SmsCode(false))
            }
            R.id.fragment_profile_resend_code_lbl -> {
                presenter!!.sendMessageAgain(getActivity())
            }
            R.id.fragment_profile_sync_back_to_phone -> {
                switchToState(ProfileViewState.SmsCode(false))
            }
            R.id.fragment_profile_sync_back_to_profile -> {
                switchToState(ProfileViewState.Profile(true))
            }
            R.id.fragment_profile_remove_profile->{
                presenter!!.removeProfile()
            }
        }
    }

    private fun readUser() {
        user!!.address = address_field!!.text.toString()
        user!!.category = category_field!!.text.toString()
        user!!.country = country_field!!.text.toString()
        user!!.name = name_field!!.text.toString()
        user!!.namegroup = group_field!!.text.toString()
        user!!.operator = operator_field!!.text.toString()
        user!!.region = region_field!!.text.toString()
    }

    private fun setFieldsInputType(text_input_type: Int) {
        name_field!!.inputType = text_input_type
        address_field!!.inputType = text_input_type
        country_field!!.inputType = text_input_type
        region_field!!.inputType = text_input_type
        operator_field!!.inputType = text_input_type
        category_field!!.inputType = text_input_type
        group_field!!.inputType = text_input_type
    }

    private fun hideKeyboard(view: View) {
        try {
            (activity!!
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: NullPointerException) {
            Log.d("msg", "Cannot hide keyboard")
        }

    }

    override fun onTimerStop() {
        vResendCodeLbl?.let {
            it.setText(R.string.resend_code_now)
            it.paintFlags = it.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            it.isEnabled = true
        }
        vSignInText!!.text = getString(R.string.sign_in)
        isTimerStopped = true
    }

    override fun onTimerUpdate(secondsLeft: Long?) {
        isTimerStopped = false
        when (viewState) {
            is ProfileViewState.Profile -> {
                if (vResendCodeLbl?.visibility == View.VISIBLE)
                    vResendCodeLbl?.visibility = View.INVISIBLE
            }
            is ProfileViewState.SmsCode -> {
                if (!(viewState as ProfileViewState.SmsCode).isCodeInput) {
                    if (vResendCodeLbl?.visibility == View.VISIBLE)
                        vResendCodeLbl?.visibility = View.INVISIBLE
                    vSignInText!!.text = getString(R.string.time_remaining, secondsLeft)
                } else {
                    if (vResendCodeLbl?.visibility != View.VISIBLE)
                        vResendCodeLbl?.visibility = View.VISIBLE
                    vResendCodeLbl?.text = getString(R.string.send_sms_again, secondsLeft)
                    vResendCodeLbl?.isEnabled = false
                }

            }
        }

    }


    override fun displayUser(user: User?) {
        if (user != null && !user.phone.isEmpty()) {
            vBackToProfile!!.visibility = View.VISIBLE
            this.user = user
            phone_number_view!!.text = user.phone
            name_field!!.setText(if (user.name == null) "" else user.name)
            address_field!!.setText(if (user.address == null) "" else user.address)
            country_field!!.setText(if (user.country == null) "" else user.country)
            region_field!!.setText(if (user.region == null) "" else user.region)
            operator_field!!.setText(if (user.operator == null) "" else user.operator)
            category_field!!.setText(if (user.category == null) "" else user.category)
            group_field!!.setText(if (user.namegroup == null) "" else user.namegroup)

            if (user.avatar.isNullOrEmpty()) {
                avatar_image!!.setImageResource(R.drawable.ic_profile)
            } else {
                Picasso.get()
                        .load(user.avatar)
                        .placeholder(R.drawable.loading)
                        .into(avatar_image)
            }
        } else {
            vBackToProfile!!.visibility = View.GONE
            switchToState(ProfileViewState.SmsCode(false))
        }
    }

    override fun displayPhoneNumber(phone: String?) {
        if (phone != null && !phone.isEmpty()) {
            vBackToProfile!!.visibility = View.VISIBLE

            phone_number_view!!.text = phone
            presenter!!.getProfile(phone, true)
            //   switchToState(ProfileViewState.Profile(false))
        } else {
            vBackToProfile!!.visibility = View.GONE
            if (viewState !is ProfileViewState.SmsCode) {
                switchToState(ProfileViewState.SmsCode(false))
            }

//            vProfileContent!!.visibility = View.GONE
//            vSyncPhone!!.visibility = View.VISIBLE
        }
    }

    override fun onNeedDisplayCode() {
        switchToState(ProfileViewState.SmsCode(true))
    }

    override fun onPhoneAttachSuccess(phone: String) {
//        vProfileContent!!.visibility = View.VISIBLE
//        vSyncPhone!!.visibility = View.GONE
//        phone_number_view!!.text = phone
        switchToState(ProfileViewState.Profile(false))
        // presenter!!.getProfile(phone, true)
    }

    override fun onPhoneAttachFailed() {
        Toast.makeText(getActivity(), "Ошибка", Toast.LENGTH_SHORT).show()
    }

    override fun onWrongCodeSend() {
        vConfirmationCodeInput!!.setTextColor(Color.RED)
    }

    override fun onUpdateFail() {
        Toast.makeText(getActivity(), "Ошибка", Toast.LENGTH_SHORT).show()
    }

    override fun onUpdateSuccess() {}

    private inner class CodeTextWatcher : TextWatcher {
        private var previousText: Editable? = null

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {
            if (s != previousText) {
                vConfirmationCodeInput!!.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGray))
            }
            previousText = s
        }
    }


    private fun createTmpFile() {
        val dir = File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/folderName")
        dir.mkdirs()

        if (dir.exists()) {
            try {
                imageFile = File.createTempFile("IMG_", ".jpg", dir)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        var isCameraPicture = false;
        if (data != null && data.getData() != null) {
            mCameraOutputFile = data.getData();
            isCameraPicture = false;
        } else {
            mCameraOutputFile = Uri.fromFile(imageFile);
            isCameraPicture = true;
        }
        if (resultCode == Activity.RESULT_OK) {

            val uri = if (isCameraPicture) {
                Picasso.get()
                        .load(imageFile!!)
                        .into(avatar_image)
                Uri.parse(imageFile.toString())
            } else {
                Picasso.get()
                        .load(mCameraOutputFile)
                        .into(avatar_image)
                mCameraOutputFile
            }
            presenter?.updateAvatar(uri)
        }
    }


    private fun openPhoto(requestCode: Int) {
        if (ActivityCompat.checkSelfPermission(activity!!, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 0)
        } else {
            val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            createTmpFile()
            camIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile))


            val gallIntent = Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            val yourIntentsList = ArrayList<Intent>()

            val listCam = activity!!.packageManager.queryIntentActivities(camIntent, 0)


            for (res in listCam) {
                val finalIntent = Intent(camIntent)
                finalIntent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
                yourIntentsList.add(finalIntent)
            }

            val listGall = activity!!.packageManager.queryIntentActivities(gallIntent, 0)
            for (res in listGall) {
                val finalIntent = Intent(gallIntent)
                finalIntent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
                yourIntentsList.add(finalIntent)
            }
            if (yourIntentsList.size > 0) {
                val chooserIntent = Intent.createChooser(yourIntentsList.removeAt(yourIntentsList.size - 1),
                        "Title")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, yourIntentsList.toTypedArray<Parcelable>())
                startActivityForResult(chooserIntent, requestCode)
            }
        }

    }
}
