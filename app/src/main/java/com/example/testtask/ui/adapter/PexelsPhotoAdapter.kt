import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.testtask.R
import com.example.testtask.model.PexelsImageWrapper
import com.example.testtask.ui.DetailsScreenActivity
import java.util.*

class PexelsPhotoAdapter(
    private val context: Context,
    private val dataSet: ArrayList<PexelsImageWrapper>
) : RecyclerView.Adapter<PexelsPhotoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView
        var progress: ProgressBar

        init {
            imageView = itemView.findViewById(R.id.imageViewItem)
            progress = itemView.findViewById(R.id.progress)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.inflate, viewGroup, false)

        val screenWidth = context.resources.displayMetrics.widthPixels
        val itemWidth = screenWidth / 2

        val layoutParams = view.layoutParams
        layoutParams.width = itemWidth.toInt()
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        view.layoutParams = layoutParams

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val image = dataSet[position]

        Glide.with(context).clear(viewHolder.imageView)

        Glide.with(context)
            .load(image.mediumUrl)
            .apply(RequestOptions().placeholder(R.drawable.placeholder_image))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    viewHolder.progress.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    viewHolder.progress.visibility = View.GONE
                    return false
                }
            })
            .into(viewHolder.imageView)

        viewHolder.imageView.clipToOutline = true // Применение закругленных углов
        viewHolder.imageView.background = context.getDrawable(R.drawable.rounded_image_background)

        viewHolder.imageView.setOnClickListener {
            val originalUrl = image.originalUrl
            val intent = Intent(context, DetailsScreenActivity::class.java)
            intent.putExtra("originalUrl", originalUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataSet.size
}